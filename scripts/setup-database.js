#!/usr/bin/env node

/**
 * ROIMSG 데이터베이스 설정 스크립트
 * PostgreSQL 데이터베이스 초기화 및 샘플 데이터 삽입
 */

const { Client } = require('pg');
const fs = require('fs');
const path = require('path');

// 데이터베이스 연결 설정
const dbConfig = {
  host: process.env.DB_HOST || 'localhost',
  port: process.env.DB_PORT || 5432,
  database: process.env.DB_NAME || 'ROIMSG',
  user: process.env.DB_USER || 'roit',
  password: process.env.DB_PASSWORD || 'fhdlxpzm1*',
};

// SQL 스크립트 파일 경로
const sqlFiles = [
  '01-create-tables.sql',
  '02-create-indexes.sql',
  '03-insert-sample-data.sql',
];

/**
 * 데이터베이스 연결 테스트
 */
async function testConnection() {
  const client = new Client(dbConfig);
  
  try {
    await client.connect();
    console.log('✅ 데이터베이스 연결 성공');
    await client.end();
    return true;
  } catch (error) {
    console.error('❌ 데이터베이스 연결 실패:', error.message);
    return false;
  }
}

/**
 * SQL 파일 실행
 */
async function executeSqlFile(client, filePath) {
  try {
    const sql = fs.readFileSync(filePath, 'utf8');
    await client.query(sql);
    console.log(`✅ ${path.basename(filePath)} 실행 완료`);
  } catch (error) {
    console.error(`❌ ${path.basename(filePath)} 실행 실패:`, error.message);
    throw error;
  }
}

/**
 * 데이터베이스 초기화
 */
async function initializeDatabase() {
  const client = new Client(dbConfig);
  
  try {
    await client.connect();
    console.log('🔄 데이터베이스 초기화 시작...');
    
    // SQL 스크립트 파일들 실행
    for (const fileName of sqlFiles) {
      const filePath = path.join(__dirname, 'sql', fileName);
      
      if (fs.existsSync(filePath)) {
        await executeSqlFile(client, filePath);
      } else {
        console.log(`⚠️  ${fileName} 파일이 존재하지 않습니다. 건너뜁니다.`);
      }
    }
    
    console.log('✅ 데이터베이스 초기화 완료');
    
  } catch (error) {
    console.error('❌ 데이터베이스 초기화 실패:', error.message);
    process.exit(1);
  } finally {
    await client.end();
  }
}

/**
 * 샘플 데이터 확인
 */
async function checkSampleData() {
  const client = new Client(dbConfig);
  
  try {
    await client.connect();
    
    // 테넌트 수 확인
    const tenantResult = await client.query('SELECT COUNT(*) FROM tenants');
    console.log(`📊 테넌트 수: ${tenantResult.rows[0].count}`);
    
    // 사용자 수 확인
    const userResult = await client.query('SELECT COUNT(*) FROM users');
    console.log(`👥 사용자 수: ${userResult.rows[0].count}`);
    
    // 게시글 수 확인
    const postResult = await client.query('SELECT COUNT(*) FROM posts');
    console.log(`📝 게시글 수: ${postResult.rows[0].count}`);
    
  } catch (error) {
    console.error('❌ 샘플 데이터 확인 실패:', error.message);
  } finally {
    await client.end();
  }
}

/**
 * 메인 함수
 */
async function main() {
  console.log('🚀 ROIMSG 데이터베이스 설정 시작');
  console.log('=====================================');
  
  // 데이터베이스 연결 테스트
  const isConnected = await testConnection();
  if (!isConnected) {
    console.log('\n💡 해결 방법:');
    console.log('1. PostgreSQL이 실행 중인지 확인하세요');
    console.log('2. 데이터베이스 설정을 확인하세요');
    console.log('3. 환경 변수를 설정하세요');
    process.exit(1);
  }
  
  // 데이터베이스 초기화
  await initializeDatabase();
  
  // 샘플 데이터 확인
  await checkSampleData();
  
  console.log('\n🎉 ROIMSG 데이터베이스 설정이 완료되었습니다!');
  console.log('이제 애플리케이션을 실행할 수 있습니다.');
}

// 스크립트 실행
if (require.main === module) {
  main().catch(console.error);
}

module.exports = {
  testConnection,
  initializeDatabase,
  checkSampleData
};
