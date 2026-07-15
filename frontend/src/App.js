import React, {useState} from 'react';
import {Button, Card, message, Space, Typography} from 'antd';
import {FileExcelOutlined, FileZipOutlined} from '@ant-design/icons';
import axios from 'axios';
import './App.css';

const {Title, Paragraph} = Typography;

function App() {
  const [loadingFile, setLoadingFile] = useState(false);
  const [loadingZip, setLoadingZip] = useState(false);

  // Базовый URL бэкенда (для разработки проксируется, для продакшена — полный)
  const API_BASE = '';
  // const API_BASE = process.env.NODE_ENV === 'development' ? '' : '/api';

  const downloadFile = async (type) => {
    const endpoint = type === 'file'
        ? '/api/v1/report/download/file'
        : '/api/v1/report/download/zip';

    const setLoading = type === 'file' ? setLoadingFile : setLoadingZip;
    setLoading(true);

    try {
      // Отправляем GET-запрос, получаем blob (бинарные данные)
      const response = await axios.get(endpoint, {
        // baseURL: 'localhost:8080/api/v1/report/download/file',
        baseURL: API_BASE,
        responseType: 'blob', // важно для бинарных файлов
      });

      // Создаём ссылку для скачивания
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      const filename = type === 'file' ? 'report.xlsx' : 'report.zip';
      link.setAttribute('download', filename);
      document.body.appendChild(link);
      link.click();
      link.parentNode.removeChild(link);
      window.URL.revokeObjectURL(url);

      message.success(`Файл ${filename} скачан`);
    } catch (error) {
      console.error('Ошибка загрузки:', error);
      message.error('Не удалось скачать файл. Попробуйте позже.');
    } finally {
      setLoading(false);
    }
  };

  return (
      <div style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '100vh',
        background: '#f0f2f5'
      }}>
        <Card style={{width: 500, textAlign: 'center', boxShadow: '0 4px 12px rgba(0,0,0,0.1)'}}>
          <Title level={2}>📊 Отчёт по случаям оказания медицинской помощи</Title>
          <Paragraph type="secondary">
            Нажмите кнопку для скачивания отчёта в нужном формате
          </Paragraph>
          <Space direction="vertical" size="large" style={{width: '100%'}}>
            <Button
                type="primary"
                icon={<FileExcelOutlined/>}
                loading={loadingFile}
                onClick={() => downloadFile('file')}
                size="large"
                block
            >
              Скачать Excel (.xlsx)
            </Button>
            <Button
                type="default"
                icon={<FileZipOutlined/>}
                loading={loadingZip}
                onClick={() => downloadFile('zip')}
                size="large"
                block
            >
              Скачать ZIP-архив
            </Button>
          </Space>
          <Paragraph type="secondary" style={{marginTop: 20}}>
            Отчёт формируется на основе данных за 2014 год.
          </Paragraph>
        </Card>
      </div>
  );
}

export default App;
