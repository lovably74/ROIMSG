<template>
  <v-container class="auth-layout" fluid>
    <v-row class="fill-height" align="center" justify="center">
      <v-col cols="12" sm="8" md="6" lg="4">
        <v-card elevation="4" class="pa-6">
          <RouterView />
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
// 인증 레이아웃은 별도 로직 없이 자식 뷰만 감싼다.
</script>

<style scoped>
.auth-layout {
  min-height: 100vh;
  background: linear-gradient(135deg, rgba(0, 123, 255, 0.15), rgba(0, 200, 188, 0.15));
}
</style>
