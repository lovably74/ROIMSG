<template>
  <v-container fluid class="file-layout">
    <v-row>
      <v-col cols="12">
        <RouterView />
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
// 파일 레이아웃
</script>

<style scoped>
.file-layout {
  padding: 24px;
}
</style>