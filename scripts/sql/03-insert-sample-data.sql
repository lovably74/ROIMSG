-- ROIMSG 샘플 데이터 삽입 스크립트

-- 기본 테넌트 생성
INSERT INTO tenants (id, name, domain, description) VALUES 
('550e8400-e29b-41d4-a716-446655440000', 'ROIMSG 기본 테넌트', 'roimsg.local', 'ROIMSG 시스템의 기본 테넌트입니다.');

-- 게시판 카테고리 생성
INSERT INTO board_categories (id, tenant_id, name, description, sort_order) VALUES 
('550e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440000', '공지사항', '중요한 공지사항을 확인하세요.', 1),
('550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440000', '자유게시판', '자유롭게 소통하는 공간입니다.', 2),
('550e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440000', '질문과 답변', '궁금한 점을 질문하고 답변을 받아보세요.', 3);

-- 샘플 사용자 생성 (Google OAuth 연동 가정)
INSERT INTO users (id, tenant_id, google_id, email, name, profile_image_url) VALUES 
('550e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440000', 'google_123456789', 'admin@roimsg.com', '관리자', 'https://via.placeholder.com/150'),
('550e8400-e29b-41d4-a716-446655440011', '550e8400-e29b-41d4-a716-446655440000', 'google_987654321', 'user1@roimsg.com', '김철수', 'https://via.placeholder.com/150'),
('550e8400-e29b-41d4-a716-446655440012', '550e8400-e29b-41d4-a716-446655440000', 'google_456789123', 'user2@roimsg.com', '이영희', 'https://via.placeholder.com/150');

-- 샘플 게시글 생성
INSERT INTO posts (id, tenant_id, category_id, author_id, title, content, is_notice) VALUES 
('550e8400-e29b-41d4-a716-446655440020', '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440010', 'ROIMSG 시스템 오픈 안내', 'ROIMSG 메시징 시스템이 정식 오픈되었습니다. 많은 이용 부탁드립니다.', true),
('550e8400-e29b-41d4-a716-446655440021', '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440011', '안녕하세요!', 'ROIMSG에 오신 것을 환영합니다. 잘 부탁드립니다!', false);

-- 샘플 댓글 생성
INSERT INTO post_comments (id, tenant_id, post_id, author_id, content) VALUES 
('550e8400-e29b-41d4-a716-446655440030', '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440021', '550e8400-e29b-41d4-a716-446655440012', '환영합니다! 함께 잘 사용해요.');

-- 샘플 태그 생성
INSERT INTO tags (id, tenant_id, name, color) VALUES 
('550e8400-e29b-41d4-a716-446655440040', '550e8400-e29b-41d4-a716-446655440000', '공지', '#ff6b6b'),
('550e8400-e29b-41d4-a716-446655440041', '550e8400-e29b-41d4-a716-446655440000', '안내', '#4ecdc4'),
('550e8400-e29b-41d4-a716-446655440042', '550e8400-e29b-41d4-a716-446655440000', '질문', '#45b7d1');

-- 시스템 설정 초기값
INSERT INTO system_settings (id, tenant_id, setting_key, setting_value, description) VALUES 
('550e8400-e29b-41d4-a716-446655440050', '550e8400-e29b-41d4-a716-446655440000', 'site_name', 'ROIMSG', '사이트 이름'),
('550e8400-e29b-41d4-a716-446655440051', '550e8400-e29b-41d4-a716-446655440000', 'max_file_size', '10485760', '최대 파일 업로드 크기 (바이트)'),
('550e8400-e29b-41d4-a716-446655440052', '550e8400-e29b-41d4-a716-446655440000', 'allowed_file_types', 'jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx,ppt,pptx,txt', '허용된 파일 확장자');
