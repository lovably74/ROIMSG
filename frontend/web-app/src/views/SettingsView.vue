<template>
  <v-container fluid class="py-6">
    <v-row justify="center">
      <v-col cols="12" md="10" lg="8">
        <v-card elevation="3">
          <v-toolbar color="primary" dark density="comfortable">
            <v-toolbar-title>환경 설정</v-toolbar-title>
          </v-toolbar>

          <v-card-text>
            <v-expansion-panels multiple>
              <v-expansion-panel value="general">
                <v-expansion-panel-title>일반 설정</v-expansion-panel-title>
                <v-expansion-panel-text>
                  <v-switch v-model="settings.darkMode" label="다크 모드" color="primary" />
                  <v-switch v-model="settings.compactMode" label="컴팩트 모드" color="primary" />
                </v-expansion-panel-text>
              </v-expansion-panel>

              <v-expansion-panel value="notification">
                <v-expansion-panel-title>알림 설정</v-expansion-panel-title>
                <v-expansion-panel-text>
                  <v-switch v-model="settings.notification.email" label="이메일 알림" color="primary" />
                  <v-switch v-model="settings.notification.push" label="푸시 알림" color="primary" />
                  <v-switch v-model="settings.notification.desktop" label="데스크톱 알림" color="primary" />
                </v-expansion-panel-text>
              </v-expansion-panel>

              <v-expansion-panel value="security">
                <v-expansion-panel-title>보안 설정</v-expansion-panel-title>
                <v-expansion-panel-text>
                  <v-btn color="primary" variant="tonal" class="mr-2">비밀번호 변경</v-btn>
                  <v-btn color="secondary" variant="tonal">2단계 인증 설정</v-btn>
                </v-expansion-panel-text>
              </v-expansion-panel>
            </v-expansion-panels>
          </v-card-text>

          <v-card-actions class="justify-end">
            <v-btn color="primary" @click="save">설정 저장</v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { reactive } from 'vue'

const settings = reactive({
  darkMode: false,
  compactMode: false,
  notification: {
    email: true,
    push: true,
    desktop: false
  }
})

const save = () => {
  console.info('설정 저장', settings)
}
</script>
