# ROIMSG UI 스타일 가이드

## 📋 개요

ROIMSG 프로젝트의 UI 디자인 시스템 가이드입니다. 이 문서는 일관된 사용자 경험을 제공하기 위한 디자인 원칙, 컴포넌트 스타일, 레이아웃 시스템을 정의합니다.

## 🎨 디자인 시스템

### 컬러 시스템 (Ocean Blue)

#### Primary Colors
```css
:root {
  --color-primary: #0ea5e9;      /* 하늘색 - 메인 액션 */
  --color-secondary: #0284c7;    /* 진한 파랑 - 보조 액션 */
  --color-accent: #38bdf8;       /* 밝은 파랑 - 강조 */
}
```

#### Semantic Colors
```css
:root {
  --color-success: #10b981;      /* 성공 */
  --color-warning: #f59e0b;      /* 경고 */
  --color-error: #ef4444;        /* 오류 */
  --color-info: #0ea5e9;         /* 정보 */
}
```

#### Neutral Colors
```css
:root {
  --color-gray-50: #f8fafc;      /* 가장 밝은 회색 */
  --color-gray-100: #f1f5f9;
  --color-gray-200: #e2e8f0;
  --color-gray-300: #cbd5e1;
  --color-gray-400: #94a3b8;
  --color-gray-500: #64748b;
  --color-gray-600: #475569;
  --color-gray-700: #334155;
  --color-gray-800: #1e293b;
  --color-gray-900: #0f172a;     /* 가장 어두운 회색 */
}
```

### 타이포그래피 (Corporate 폰트 시스템)

#### 폰트 패밀리
```css
:root {
  --font-family-primary: 'Roboto', '본고딕', Arial, sans-serif;
  --font-family-mono: 'Roboto Mono', 'Consolas', monospace;
}
```

#### 폰트 크기
```css
:root {
  --font-size-xs: 0.75rem;    /* 12px */
  --font-size-sm: 0.875rem;   /* 14px */
  --font-size-base: 1rem;     /* 16px - 기준 */
  --font-size-lg: 1.125rem;   /* 18px */
  --font-size-xl: 1.25rem;    /* 20px */
  --font-size-2xl: 1.5rem;    /* 24px */
  --font-size-3xl: 1.875rem;  /* 30px */
  --font-size-4xl: 2.25rem;   /* 36px */
  --font-size-5xl: 3rem;      /* 48px */
}
```

#### 폰트 두께
```css
:root {
  --font-weight-light: 300;
  --font-weight-normal: 400;
  --font-weight-medium: 500;
  --font-weight-semibold: 600;
  --font-weight-bold: 700;
}
```

#### 행간
```css
:root {
  --line-height-tight: 1.25;
  --line-height-normal: 1.4;
  --line-height-relaxed: 1.5;
}
```

### 간격 시스템

```css
:root {
  --spacing-1: 0.25rem;   /* 4px */
  --spacing-2: 0.5rem;    /* 8px */
  --spacing-3: 0.75rem;   /* 12px */
  --spacing-4: 1rem;      /* 16px */
  --spacing-5: 1.25rem;   /* 20px */
  --spacing-6: 1.5rem;    /* 24px */
  --spacing-8: 2rem;      /* 32px */
  --spacing-10: 2.5rem;   /* 40px */
  --spacing-12: 3rem;     /* 48px */
  --spacing-16: 4rem;     /* 64px */
  --spacing-20: 5rem;     /* 80px */
  --spacing-24: 6rem;     /* 96px */
}
```

### 그림자 시스템

```css
:root {
  --shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  --shadow-base: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06);
  --shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
  --shadow-xl: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}
```

## 🏗️ 레이아웃 시스템

### 대시보드 레이아웃

#### 컨테이너
```css
.dashboard-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--spacing-6);
}
```

#### 사이드바
```css
.sidebar {
  width: 280px;
  position: sticky;
  top: 0;
  height: 100vh;
  background: var(--color-gray-50);
  border-right: 1px solid var(--color-gray-200);
}
```

#### 헤더
```css
.header {
  height: 64px;
  background: white;
  box-shadow: var(--shadow-sm);
  display: flex;
  align-items: center;
  padding: 0 var(--spacing-6);
}
```

#### 메인 컨텐츠
```css
.main-content {
  flex: 1;
  padding: var(--spacing-6);
  background: var(--color-gray-50);
  min-height: calc(100vh - 64px);
}
```

### CSS Grid 시스템

```css
.grid-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--spacing-8);
  width: 100%;
}

/* 반응형 그리드 */
@media (max-width: 768px) {
  .grid-container {
    grid-template-columns: 1fr;
    gap: var(--spacing-4);
  }
}
```

## 🎭 Neumorphism 컴포넌트 스타일

### 기본 Neumorphism 스타일
```css
.neumorphism {
  background: #e0e5ec;
  border-radius: 16px;
  box-shadow: 
    8px 8px 16px #d1d9e6,
    -8px -8px 16px #f9f9f9;
  transition: all 0.3s ease;
}

.neumorphism:hover {
  box-shadow: 
    inset 4px 4px 8px #d1d9e6,
    inset -4px -4px 8px #f9f9f9;
}

.neumorphism:active {
  box-shadow: 
    inset 6px 6px 12px #d1d9e6,
    inset -6px -6px 12px #f9f9f9;
}
```

### 버튼 스타일
```css
.btn-primary {
  background: var(--color-primary);
  color: white;
  border: none;
  border-radius: 12px;
  padding: var(--spacing-3) var(--spacing-6);
  font-weight: var(--font-weight-medium);
  box-shadow: 
    4px 4px 8px rgba(14, 165, 233, 0.3),
    -4px -4px 8px rgba(14, 165, 233, 0.1);
  transition: all 0.3s ease;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 
    6px 6px 12px rgba(14, 165, 233, 0.4),
    -6px -6px 12px rgba(14, 165, 233, 0.2);
}

.btn-secondary {
  background: var(--color-gray-100);
  color: var(--color-gray-700);
  border: none;
  border-radius: 12px;
  padding: var(--spacing-3) var(--spacing-6);
  font-weight: var(--font-weight-medium);
  box-shadow: 
    4px 4px 8px #d1d9e6,
    -4px -4px 8px #f9f9f9;
  transition: all 0.3s ease;
}
```

### 카드 스타일
```css
.card {
  background: white;
  border-radius: 16px;
  padding: var(--spacing-6);
  box-shadow: 
    8px 8px 16px #d1d9e6,
    -8px -8px 16px #f9f9f9;
  transition: all 0.3s ease;
}

.card:hover {
  transform: translateY(-4px);
  box-shadow: 
    12px 12px 24px #d1d9e6,
    -12px -12px 24px #f9f9f9;
}
```

### 입력 필드 스타일
```css
.input-field {
  background: #e0e5ec;
  border: none;
  border-radius: 12px;
  padding: var(--spacing-4);
  font-size: var(--font-size-base);
  box-shadow: 
    inset 4px 4px 8px #d1d9e6,
    inset -4px -4px 8px #f9f9f9;
  transition: all 0.3s ease;
}

.input-field:focus {
  outline: none;
  box-shadow: 
    inset 6px 6px 12px #d1d9e6,
    inset -6px -6px 12px #f9f9f9,
    0 0 0 3px rgba(14, 165, 233, 0.1);
}
```

## 🎬 애니메이션 시스템

### Rotate & Flip 인터랙션
```css
.rotate-flip {
  transition: transform 0.6s ease-in-out;
  transform-style: preserve-3d;
  perspective: 1000px;
}

.rotate-flip:hover {
  transform: rotateY(180deg);
}

.rotate-flip.flip-x:hover {
  transform: rotateX(180deg);
}

.rotate-flip.flip-z:hover {
  transform: rotateZ(180deg);
}
```

### 로딩 애니메이션
```css
.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid var(--color-gray-200);
  border-top: 4px solid var(--color-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
```

### 페이드 애니메이션
```css
.fade-in {
  animation: fadeIn 0.5s ease-in-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.fade-out {
  animation: fadeOut 0.3s ease-in-out;
}

@keyframes fadeOut {
  from { opacity: 1; transform: translateY(0); }
  to { opacity: 0; transform: translateY(-20px); }
}
```

## 📱 반응형 디자인

### 브레이크포인트
```css
:root {
  --breakpoint-sm: 640px;
  --breakpoint-md: 768px;
  --breakpoint-lg: 1024px;
  --breakpoint-xl: 1280px;
  --breakpoint-2xl: 1536px;
}
```

### 반응형 유틸리티 클래스
```css
/* 모바일 우선 */
.hidden-mobile {
  display: block;
}

@media (max-width: 767px) {
  .hidden-mobile {
    display: none;
  }
}

/* 태블릿 이상 */
.visible-tablet {
  display: none;
}

@media (min-width: 768px) {
  .visible-tablet {
    display: block;
  }
}

/* 데스크톱 이상 */
.visible-desktop {
  display: none;
}

@media (min-width: 1024px) {
  .visible-desktop {
    display: block;
  }
}
```

## ♿ 접근성 고려사항

### 포커스 스타일
```css
.focus-visible {
  outline: 2px solid var(--color-primary);
  outline-offset: 2px;
  border-radius: 4px;
}

/* 키보드 포커스만 표시 */
*:focus:not(:focus-visible) {
  outline: none;
}
```

### 색상 대비
```css
/* AA 레벨 (4.5:1) */
.text-aa {
  color: var(--color-gray-700);
  background: var(--color-gray-100);
}

/* AAA 레벨 (7:1) */
.text-aaa {
  color: var(--color-gray-900);
  background: var(--color-gray-50);
}
```

### 스크린 리더 전용 텍스트
```css
.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}
```

## 🚀 성능 최적화

### 이미지 최적화
```css
.optimized-image {
  image-rendering: -webkit-optimize-contrast;
  image-rendering: crisp-edges;
  object-fit: cover;
  object-position: center;
}
```

### 애니메이션 성능
```css
.performance-optimized {
  will-change: transform;
  transform: translateZ(0);
  backface-visibility: hidden;
}
```

### 지연 로딩
```css
.lazy-load {
  opacity: 0;
  transition: opacity 0.3s ease;
}

.lazy-load.loaded {
  opacity: 1;
}
```

## 📋 컴포넌트 예시

### 네비게이션 메뉴
```html
<nav class="sidebar">
  <div class="nav-header">
    <h2 class="nav-title">ROIMSG</h2>
  </div>
  <ul class="nav-menu">
    <li class="nav-item">
      <a href="/dashboard" class="nav-link neumorphism">
        <i class="nav-icon">🏠</i>
        <span class="nav-text">대시보드</span>
      </a>
    </li>
    <li class="nav-item">
      <a href="/messages" class="nav-link neumorphism">
        <i class="nav-icon">💬</i>
        <span class="nav-text">메시지</span>
      </a>
    </li>
  </ul>
</nav>
```

### 메시지 카드
```html
<div class="message-card card">
  <div class="message-header">
    <img src="avatar.jpg" alt="사용자 아바타" class="avatar">
    <div class="message-info">
      <h3 class="sender-name">김철수</h3>
      <span class="message-time">2시간 전</span>
    </div>
  </div>
  <div class="message-content">
    <p>안녕하세요! 프로젝트 진행 상황은 어떤가요?</p>
  </div>
  <div class="message-actions">
    <button class="btn-secondary">답장</button>
    <button class="btn-primary">전달</button>
  </div>
</div>
```

### 폼 컴포넌트
```html
<form class="message-form">
  <div class="form-group">
    <label for="recipient" class="form-label">받는 사람</label>
    <input 
      type="text" 
      id="recipient" 
      class="input-field" 
      placeholder="이름 또는 이메일을 입력하세요"
      required
    >
  </div>
  <div class="form-group">
    <label for="message" class="form-label">메시지</label>
    <textarea 
      id="message" 
      class="input-field" 
      rows="4" 
      placeholder="메시지를 입력하세요"
      required
    ></textarea>
  </div>
  <div class="form-actions">
    <button type="button" class="btn-secondary">취소</button>
    <button type="submit" class="btn-primary">전송</button>
  </div>
</form>
```

## 📚 사용 가이드

### 1. 컬러 사용
- Primary 컬러는 주요 액션 버튼에만 사용
- Secondary 컬러는 보조 액션에 사용
- Semantic 컬러는 상태 표시에만 사용

### 2. 타이포그래피
- 본문은 16px (1rem) 기준
- 제목은 계층에 따라 1.2배율 적용
- 행간은 1.4-1.5 유지

### 3. 간격
- 8px 단위로 일관된 간격 사용
- 컴포넌트 간 간격은 24px (1.5rem) 이상

### 4. 애니메이션
- 0.3s 이하의 빠른 트랜지션 사용
- 과도한 애니메이션 지양
- 접근성 설정 고려

### 5. 반응형
- 모바일 우선 설계
- 터치 친화적 인터페이스
- 최소 44px 터치 영역 확보

---

**문서 버전**: v1.0  
**작성일**: 2025-10-02  
**작성자**: ROIMSG Development Team  
**참고 문서**: UI-마크다운-2025-09-30.md
