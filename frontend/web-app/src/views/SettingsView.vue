<template>
  <v-container fluid class="py-6">
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">설정</h1>
        
        <v-card elevation="2" class="pa-6">
          <v-tabs v-model="activeTab" class="mb-6">
            <v-tab value="general">일반</v-tab>
            <v-tab value="notifications">알림</v-tab>
            <v-tab value="security">보안</v-tab>
          </v-tabs>
          
          <v-tabs-window v-model="activeTab">
            <v-tabs-window-item value="general">
              <v-row>
                <v-col cols="12" md="6">
                  <h3 class="text-h6 mb-4">일반 설정</h3>
                  
                  <v-select
                    v-model="settings.language"
                    label="언어"
                    :items="languages"
                    variant="outlined"
                    class="mb-4"
                  />
                  
                  <v-select
                    v-model="settings.theme"
                    label="테마"
                    :items="themes"
                    variant="outlined"
                    class="mb-4"
                  />
                  
                  <v-btn color="primary" @click="saveSettings">
                    설정 저장
                  </v-btn>
                </v-col>
              </v-row>
            </v-tabs-window-item>
            
            <v-tabs-window-item value="notifications">
              <v-row>
                <v-col cols="12" md="6">
                  <h3 class="text-h6 mb-4">알림 설정</h3>
                  
                  <v-switch
                    v-model="settings.emailNotifications"
                    label="이메일 알림"
                    class="mb-4"
                  />
                  
                  <v-switch
                    v-model="settings.pushNotifications"
                    label="푸시 알림"
                    class="mb-4"
                  />
                  
                  <v-switch
                    v-model="settings.messageNotifications"
                    label="메시지 알림"
                    class="mb-4"
                  />
                  
                  <v-btn color="primary" @click="saveSettings">
                    설정 저장
                  </v-btn>
                </v-col>
              </v-row>
            </v-tabs-window-item>
            
            <v-tabs-window-item value="security">
              <v-row>
                <v-col cols="12" md="6">
                  <h3 class="text-h6 mb-4">보안 설정</h3>
                  
                  <v-switch
                    v-model="settings.twoFactorAuth"
                    label="2단계 인증"
                    class="mb-4"
                  />
                  
                  <v-switch
                    v-model="settings.sessionTimeout"
                    label="세션 타임아웃"
                    class="mb-4"
                  />
                  
                  <v-btn color="primary" @click="saveSettings">
                    설정 저장
                  </v-btn>
                </v-col>
              </v-row>
            </v-tabs-window-item>
          </v-tabs-window>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'

const activeTab = ref('general')

const settings = reactive({
  language: 'ko',
  theme: 'light',
  emailNotifications: true,
  pushNotifications: true,
  messageNotifications: true,
  twoFactorAuth: false,
  sessionTimeout: true
})

const languages = [
  { title: '한국어', value: 'ko' },
  { title: 'English', value: 'en' }
]

const themes = [
  { title: '라이트', value: 'light' },
  { title: '다크', value: 'dark' },
  { title: '자동', value: 'auto' }
]

const saveSettings = () => {
  // 설정 저장 로직
  console.log('설정 저장:', settings)
}
</script>

<style scoped>
/* 설정 스타일 */
</style>