-- 게시글 테이블 업데이트 스크립트
-- 기존 posts 테이블에 누락된 필드들을 추가

-- 기존 테이블이 있다면 컬럼 추가 (이미 존재하는 경우 무시)
DO $$ 
BEGIN
    -- category 컬럼 추가
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'posts' AND column_name = 'category') THEN
        ALTER TABLE posts ADD COLUMN category VARCHAR(50) DEFAULT 'general';
    END IF;
    
    -- allow_comments 컬럼 추가
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'posts' AND column_name = 'allow_comments') THEN
        ALTER TABLE posts ADD COLUMN allow_comments BOOLEAN DEFAULT true;
    END IF;
    
    -- views 컬럼 추가 (view_count가 있다면 이름 변경)
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'posts' AND column_name = 'view_count') THEN
        ALTER TABLE posts RENAME COLUMN view_count TO views;
    ELSIF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                      WHERE table_name = 'posts' AND column_name = 'views') THEN
        ALTER TABLE posts ADD COLUMN views INTEGER DEFAULT 0;
    END IF;
    
    -- likes 컬럼 추가 (like_count가 있다면 이름 변경)
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'posts' AND column_name = 'like_count') THEN
        ALTER TABLE posts RENAME COLUMN like_count TO likes;
    ELSIF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                      WHERE table_name = 'posts' AND column_name = 'likes') THEN
        ALTER TABLE posts ADD COLUMN likes INTEGER DEFAULT 0;
    END IF;
    
    -- 불필요한 컬럼들 제거 (있다면)
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'posts' AND column_name = 'category_id') THEN
        ALTER TABLE posts DROP COLUMN IF EXISTS category_id;
    END IF;
    
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'posts' AND column_name = 'dislike_count') THEN
        ALTER TABLE posts DROP COLUMN IF EXISTS dislike_count;
    END IF;
    
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'posts' AND column_name = 'is_active') THEN
        ALTER TABLE posts DROP COLUMN IF EXISTS is_active;
    END IF;
END $$;

-- 기존 데이터의 기본값 설정
UPDATE posts SET 
    category = COALESCE(category, 'general'),
    is_notice = COALESCE(is_notice, false),
    is_secret = COALESCE(is_secret, false),
    allow_comments = COALESCE(allow_comments, true),
    views = COALESCE(views, 0),
    likes = COALESCE(likes, 0)
WHERE category IS NULL 
   OR is_notice IS NULL 
   OR is_secret IS NULL 
   OR allow_comments IS NULL 
   OR views IS NULL 
   OR likes IS NULL;

