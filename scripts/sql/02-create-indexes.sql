-- ROIMSG 데이터베이스 인덱스 생성 스크립트
-- 성능 최적화를 위한 인덱스 설정

-- 테넌트 관련 인덱스
CREATE INDEX IF NOT EXISTS idx_tenants_domain ON tenants(domain) WHERE domain IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_tenants_active ON tenants(is_active);

-- 사용자 관련 인덱스
CREATE INDEX IF NOT EXISTS idx_users_tenant_id ON users(tenant_id);
CREATE INDEX IF NOT EXISTS idx_users_google_id ON users(google_id) WHERE google_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_tenant_email ON users(tenant_id, email);
CREATE INDEX IF NOT EXISTS idx_users_active ON users(is_active);
CREATE INDEX IF NOT EXISTS idx_users_last_login ON users(last_login_at);

-- 채팅방 관련 인덱스
CREATE INDEX IF NOT EXISTS idx_chat_rooms_tenant_id ON chat_rooms(tenant_id);
CREATE INDEX IF NOT EXISTS idx_chat_rooms_created_by ON chat_rooms(created_by);
CREATE INDEX IF NOT EXISTS idx_chat_rooms_type ON chat_rooms(room_type);
CREATE INDEX IF NOT EXISTS idx_chat_rooms_active ON chat_rooms(is_active);

-- 채팅방 멤버 관련 인덱스
CREATE INDEX IF NOT EXISTS idx_chat_room_members_tenant_id ON chat_room_members(tenant_id);
CREATE INDEX IF NOT EXISTS idx_chat_room_members_room_id ON chat_room_members(room_id);
CREATE INDEX IF NOT EXISTS idx_chat_room_members_user_id ON chat_room_members(user_id);
CREATE INDEX IF NOT EXISTS idx_chat_room_members_active ON chat_room_members(is_active);

-- 메시지 관련 인덱스
CREATE INDEX IF NOT EXISTS idx_messages_tenant_id ON messages(tenant_id);
CREATE INDEX IF NOT EXISTS idx_messages_room_id ON messages(room_id);
CREATE INDEX IF NOT EXISTS idx_messages_sender_id ON messages(sender_id);
CREATE INDEX IF NOT EXISTS idx_messages_receiver_id ON messages(receiver_id);
CREATE INDEX IF NOT EXISTS idx_messages_created_at ON messages(created_at);
CREATE INDEX IF NOT EXISTS idx_messages_type ON messages(message_type);
CREATE INDEX IF NOT EXISTS idx_messages_read ON messages(is_read);

-- 게시판 카테고리 관련 인덱스
CREATE INDEX IF NOT EXISTS idx_board_categories_tenant_id ON board_categories(tenant_id);
CREATE INDEX IF NOT EXISTS idx_board_categories_active ON board_categories(is_active);
CREATE INDEX IF NOT EXISTS idx_board_categories_sort_order ON board_categories(sort_order);

-- 게시글 관련 인덱스
CREATE INDEX IF NOT EXISTS idx_posts_tenant_id ON posts(tenant_id);
CREATE INDEX IF NOT EXISTS idx_posts_category_id ON posts(category_id);
CREATE INDEX IF NOT EXISTS idx_posts_author_id ON posts(author_id);
CREATE INDEX IF NOT EXISTS idx_posts_created_at ON posts(created_at);
CREATE INDEX IF NOT EXISTS idx_posts_updated_at ON posts(updated_at);
CREATE INDEX IF NOT EXISTS idx_posts_notice ON posts(is_notice);
CREATE INDEX IF NOT EXISTS idx_posts_secret ON posts(is_secret);
CREATE INDEX IF NOT EXISTS idx_posts_active ON posts(is_active);
CREATE INDEX IF NOT EXISTS idx_posts_view_count ON posts(view_count);
CREATE INDEX IF NOT EXISTS idx_posts_like_count ON posts(like_count);

-- 게시글 제목 및 내용 검색을 위한 텍스트 인덱스
CREATE INDEX IF NOT EXISTS idx_posts_title_gin ON posts USING gin(to_tsvector('english', title));
CREATE INDEX IF NOT EXISTS idx_posts_content_gin ON posts USING gin(to_tsvector('english', content));

-- 게시글 댓글 관련 인덱스
CREATE INDEX IF NOT EXISTS idx_post_comments_tenant_id ON post_comments(tenant_id);
CREATE INDEX IF NOT EXISTS idx_post_comments_post_id ON post_comments(post_id);
CREATE INDEX IF NOT EXISTS idx_post_comments_parent_id ON post_comments(parent_id);
CREATE INDEX IF NOT EXISTS idx_post_comments_author_id ON post_comments(author_id);
CREATE INDEX IF NOT EXISTS idx_post_comments_created_at ON post_comments(created_at);
CREATE INDEX IF NOT EXISTS idx_post_comments_active ON post_comments(is_active);

-- 게시글 첨부파일 관련 인덱스
CREATE INDEX IF NOT EXISTS idx_post_attachments_tenant_id ON post_attachments(tenant_id);
CREATE INDEX IF NOT EXISTS idx_post_attachments_post_id ON post_attachments(post_id);

-- 태그 관련 인덱스
CREATE INDEX IF NOT EXISTS idx_tags_tenant_id ON tags(tenant_id);
CREATE INDEX IF NOT EXISTS idx_tags_name ON tags(name);

-- 게시글-태그 연결 관련 인덱스
CREATE INDEX IF NOT EXISTS idx_post_tags_tenant_id ON post_tags(tenant_id);
CREATE INDEX IF NOT EXISTS idx_post_tags_post_id ON post_tags(post_id);
CREATE INDEX IF NOT EXISTS idx_post_tags_tag_id ON post_tags(tag_id);

-- 파일 관련 인덱스
CREATE INDEX IF NOT EXISTS idx_files_tenant_id ON files(tenant_id);
CREATE INDEX IF NOT EXISTS idx_files_uploader_id ON files(uploader_id);
CREATE INDEX IF NOT EXISTS idx_files_created_at ON files(created_at);
CREATE INDEX IF NOT EXISTS idx_files_active ON files(is_active);
CREATE INDEX IF NOT EXISTS idx_files_download_count ON files(download_count);
CREATE INDEX IF NOT EXISTS idx_files_mime_type ON files(mime_type);

-- 파일명 검색을 위한 텍스트 인덱스
CREATE INDEX IF NOT EXISTS idx_files_name_gin ON files USING gin(to_tsvector('english', file_name));
CREATE INDEX IF NOT EXISTS idx_files_original_name_gin ON files USING gin(to_tsvector('english', original_name));

-- 파일-태그 연결 관련 인덱스
CREATE INDEX IF NOT EXISTS idx_file_tags_tenant_id ON file_tags(tenant_id);
CREATE INDEX IF NOT EXISTS idx_file_tags_file_id ON file_tags(file_id);
CREATE INDEX IF NOT EXISTS idx_file_tags_tag_id ON file_tags(tag_id);

-- 게시글 반응 관련 인덱스
CREATE INDEX IF NOT EXISTS idx_post_reactions_tenant_id ON post_reactions(tenant_id);
CREATE INDEX IF NOT EXISTS idx_post_reactions_post_id ON post_reactions(post_id);
CREATE INDEX IF NOT EXISTS idx_post_reactions_user_id ON post_reactions(user_id);
CREATE INDEX IF NOT EXISTS idx_post_reactions_type ON post_reactions(reaction_type);

-- 댓글 반응 관련 인덱스
CREATE INDEX IF NOT EXISTS idx_comment_reactions_tenant_id ON comment_reactions(tenant_id);
CREATE INDEX IF NOT EXISTS idx_comment_reactions_comment_id ON comment_reactions(comment_id);
CREATE INDEX IF NOT EXISTS idx_comment_reactions_user_id ON comment_reactions(user_id);
CREATE INDEX IF NOT EXISTS idx_comment_reactions_type ON comment_reactions(reaction_type);

-- 알림 관련 인덱스
CREATE INDEX IF NOT EXISTS idx_notifications_tenant_id ON notifications(tenant_id);
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_type ON notifications(type);
CREATE INDEX IF NOT EXISTS idx_notifications_read ON notifications(is_read);
CREATE INDEX IF NOT EXISTS idx_notifications_created_at ON notifications(created_at);

-- 사용자 세션 관련 인덱스
CREATE INDEX IF NOT EXISTS idx_user_sessions_tenant_id ON user_sessions(tenant_id);
CREATE INDEX IF NOT EXISTS idx_user_sessions_user_id ON user_sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_user_sessions_token ON user_sessions(session_token);
CREATE INDEX IF NOT EXISTS idx_user_sessions_expires_at ON user_sessions(expires_at);
CREATE INDEX IF NOT EXISTS idx_user_sessions_active ON user_sessions(is_active);

-- 시스템 설정 관련 인덱스
CREATE INDEX IF NOT EXISTS idx_system_settings_tenant_id ON system_settings(tenant_id);
CREATE INDEX IF NOT EXISTS idx_system_settings_key ON system_settings(setting_key);

-- 복합 인덱스 (자주 함께 사용되는 컬럼들)
CREATE INDEX IF NOT EXISTS idx_messages_room_created ON messages(room_id, created_at);
CREATE INDEX IF NOT EXISTS idx_messages_tenant_created ON messages(tenant_id, created_at);
CREATE INDEX IF NOT EXISTS idx_posts_tenant_created ON posts(tenant_id, created_at);
CREATE INDEX IF NOT EXISTS idx_posts_category_created ON posts(category_id, created_at);
CREATE INDEX IF NOT EXISTS idx_files_tenant_created ON files(tenant_id, created_at);
CREATE INDEX IF NOT EXISTS idx_notifications_user_created ON notifications(user_id, created_at);
CREATE INDEX IF NOT EXISTS idx_notifications_user_read ON notifications(user_id, is_read);

-- 부분 인덱스 (특정 조건에서만 인덱스 적용)
CREATE INDEX IF NOT EXISTS idx_posts_active_notice ON posts(created_at) WHERE is_active = true AND is_notice = true;
CREATE INDEX IF NOT EXISTS idx_messages_unread ON messages(created_at) WHERE is_read = false;
CREATE INDEX IF NOT EXISTS idx_notifications_unread ON notifications(created_at) WHERE is_read = false;

-- 통계 쿼리 최적화를 위한 인덱스
CREATE INDEX IF NOT EXISTS idx_posts_stats ON posts(tenant_id, created_at, is_active);
CREATE INDEX IF NOT EXISTS idx_messages_stats ON messages(tenant_id, created_at);
CREATE INDEX IF NOT EXISTS idx_files_stats ON files(tenant_id, created_at, is_active);

-- 멀티테넌시 쿼리 최적화를 위한 인덱스
CREATE INDEX IF NOT EXISTS idx_users_tenant_active ON users(tenant_id, is_active);
CREATE INDEX IF NOT EXISTS idx_posts_tenant_active ON posts(tenant_id, is_active);
CREATE INDEX IF NOT EXISTS idx_files_tenant_active ON files(tenant_id, is_active);
CREATE INDEX IF NOT EXISTS idx_messages_tenant_room ON messages(tenant_id, room_id);
