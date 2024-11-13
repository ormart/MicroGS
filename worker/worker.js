require('dotenv').config();
const amqp = require('amqplib');
const puppeteer = require('puppeteer');
const Minio = require('minio');
const fs = require('fs').promises;
const path = require('path');
const redis = require('redis');
const { promisify } = require('util');

const minioClient = new Minio.Client({
    endPoint: process.env.MINIO_ENDPOINT,
    port: 9000,
    useSSL: false,
    accessKey: process.env.MINIO_ACCESS_KEY,
    secretKey: process.env.MINIO_SECRET_KEY
});

const redisClient = redis.createClient({
    host: process.env.REDIS_HOST,
    port: process.env.REDIS_PORT
});
const setAsync = promisify(redisClient.set).bind(redisClient);
const expireAsync = promisify(redisClient.expire).bind(redisClient);

async function startWorker() {
    try {
        const connection = await amqp.connect(process.env.RABBITMQ_URL);
        const channel = await connection.createChannel();
        await channel.assertQueue(process.env.QUEUE_NAME, { durable: true });

        console.log(`Worker is waiting for messages in ${process.env.QUEUE_NAME}`);

        channel.consume(process.env.QUEUE_NAME, async (msg) => {
            if (msg !== null) {
                const messageContent = msg.content.toString();
                console.log(`Received message: ${messageContent}`);

                const diplomaData = JSON.parse(messageContent);
                await processDiploma(diplomaData);

                channel.ack(msg);
            }
        }, { noAck: false });
    } catch (error) {
        console.error('Failed to start worker:', error);
    }
}

async function processDiploma(diplomaData) {
    try {
        // Carregar template HTML e substituir placeholders
        const templatePath = path.resolve(__dirname, 'template-diploma.html');
        let templateHtml = await fs.readFile(templatePath, 'utf8');
        templateHtml = templateHtml.replace('{{nome_aluno}}', diplomaData.nomeAluno)
            .replace('{{curso}}', diplomaData.nomeCurso)
            .replace('{{data_conclusao}}', diplomaData.dataConclusao);

        // Gerar PDF com Puppeteer
        const browser = await puppeteer.launch({
            executablePath: process.env.CHROME_EXECUTABLE_PATH,
            headless: true,
            args: ['--no-sandbox', '--disable-setuid-sandbox']
        });

        const page = await browser.newPage();
        await page.setContent(templateHtml);

        const pdfBuffer = await page.pdf({
            format: 'A4',
            printBackground: true
        });

        await browser.close();

        // Salvar no MinIO
        const pdfFilePath = `diplomas/${diplomaData.nomeAluno}_${diplomaData.dataConclusao}.pdf`;
        const tempFilePath = path.resolve(__dirname, `${diplomaData.nomeAluno}_${diplomaData.dataConclusao}.pdf`);
        await fs.writeFile(tempFilePath, pdfBuffer);

        await minioClient.fPutObject(process.env.MINIO_BUCKET, pdfFilePath, tempFilePath, {
            'Content-Type': 'application/pdf'
        });

        await fs.unlink(tempFilePath);

        console.log(`Diploma generated and saved to MinIO: ${pdfFilePath}`);

        // Armazenar no Redis
        const cacheKey = `diploma:${diplomaData.id}`;
        const cacheValue = JSON.stringify(diplomaData);
        await setAsync(cacheKey, cacheValue);
        await expireAsync(cacheKey, 3600); // Expiração de 1 hora

        console.log(`Diploma cached with key ${cacheKey}`);
    } catch (error) {
        console.error('Failed to process diploma:', error);
    }
}

startWorker();
