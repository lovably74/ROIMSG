<template>
  <v-container fluid class="board-layout">
    <v-row>
      <v-col cols="12">
        <RouterView />
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
// 게시판 레이아웃
</script>

<style scoped>
.board-layout {
  padding: 24px;
}
</style>