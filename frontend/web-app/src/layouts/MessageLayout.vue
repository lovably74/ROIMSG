<template>
  <v-container fluid class="message-layout">
    <v-row>
      <v-col cols="12">
        <RouterView />
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
// 메시지 레이아웃
</script>

<style scoped>
.message-layout {
  padding: 24px;
}
</style>