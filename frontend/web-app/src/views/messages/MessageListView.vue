<template>
  <v-container fluid class="py-6">
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">메시지</h1>
        
        <v-row>
          <v-col cols="12" md="4">
            <v-card elevation="2" class="pa-4">
              <h3 class="text-h6 mb-4">채팅방 목록</h3>
              
              <v-list>
                <v-list-item
                  v-for="room in chatRooms"
                  :key="room.id"
                  @click="selectRoom(room)"
                  :class="{ 'bg-primary-lighten-5': selectedRoom?.id === room.id }"
                >
                  <template v-slot:prepend>
                    <v-avatar size="40">
                      <v-img v-if="room.avatar" :src="room.avatar" />
                      <v-icon v-else>mdi-account</v-icon>
                    </v-avatar>
                  </template>
                  
                  <v-list-item-title>{{ room.name }}</v-list-item-title>
                  <v-list-item-subtitle>{{ room.lastMessage }}</v-list-item-subtitle>
                  
                  <template v-slot:append>
                    <v-chip
                      v-if="room.unreadCount > 0"
                      color="primary"
                      size="small"
                    >
                      {{ room.unreadCount }}
                    </v-chip>
                    <span class="text-caption text-medium-emphasis">
                      {{ room.lastMessageTime }}
                    </span>
                  </template>
                </v-list-item>
              </v-list>
            </v-card>
          </v-col>
          
          <v-col cols="12" md="8">
            <v-card elevation="2" class="message-container">
              <v-card-title v-if="selectedRoom" class="d-flex align-center">
                <v-avatar size="32" class="mr-3">
                  <v-img v-if="selectedRoom.avatar" :src="selectedRoom.avatar" />
                  <v-icon v-else>mdi-account</v-icon>
                </v-avatar>
                {{ selectedRoom.name }}
              </v-card-title>
              
              <v-card-text v-if="selectedRoom" class="message-content">
                <div
                  v-for="message in messages"
                  :key="message.id"
                  :class="['message-item', { 'own-message': message.isOwn }]"
                >
                  <div class="message-bubble">
                    <p class="mb-1">{{ message.content }}</p>
                    <span class="text-caption text-medium-emphasis">
                      {{ message.timestamp }}
                    </span>
                  </div>
                </div>
              </v-card-text>
              
              <v-card-actions v-if="selectedRoom" class="message-input">
                <v-text-field
                  v-model="newMessage"
                  placeholder="메시지를 입력하세요..."
                  variant="outlined"
                  density="compact"
                  hide-details
                  @keyup.enter="sendMessage"
                />
                <v-btn
                  color="primary"
                  @click="sendMessage"
                  :disabled="!newMessage.trim()"
                >
                  전송
                </v-btn>
              </v-card-actions>
              
              <div v-else class="text-center pa-8">
                <v-icon size="64" color="grey-lighten-1">mdi-message-outline</v-icon>
                <p class="text-h6 text-medium-emphasis mt-4">채팅방을 선택하세요</p>
              </div>
            </v-card>
          </v-col>
        </v-row>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'

const selectedRoom = ref(null)
const newMessage = ref('')

const chatRooms = ref([
  {
    id: '1',
    name: '일반 채팅',
    avatar: null,
    lastMessage: '안녕하세요!',
    lastMessageTime: '10:30',
    unreadCount: 2
  },
  {
    id: '2',
    name: '프로젝트 팀',
    avatar: null,
    lastMessage: '회의 준비는 되셨나요?',
    lastMessageTime: '09:15',
    unreadCount: 0
  },
  {
    id: '3',
    name: '김철수',
    avatar: null,
    lastMessage: '감사합니다!',
    lastMessageTime: '어제',
    unreadCount: 1
  }
])

const messages = ref([
  {
    id: '1',
    content: '안녕하세요!',
    timestamp: '10:30',
    isOwn: false
  },
  {
    id: '2',
    content: '네, 안녕하세요!',
    timestamp: '10:31',
    isOwn: true
  },
  {
    id: '3',
    content: '오늘 날씨가 정말 좋네요.',
    timestamp: '10:32',
    isOwn: false
  }
])

const selectRoom = (room: any) => {
  selectedRoom.value = room
  // 채팅방 메시지 로드
}

const sendMessage = () => {
  if (!newMessage.value.trim()) return
  
  const message = {
    id: Date.now().toString(),
    content: newMessage.value,
    timestamp: new Date().toLocaleTimeString('ko-KR', { 
      hour: '2-digit', 
      minute: '2-digit' 
    }),
    isOwn: true
  }
  
  messages.value.push(message)
  newMessage.value = ''
}
</script>

<style scoped>
.message-container {
  height: 600px;
  display: flex;
  flex-direction: column;
}

.message-content {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.message-item {
  margin-bottom: 12px;
  display: flex;
}

.message-item.own-message {
  justify-content: flex-end;
}

.message-bubble {
  max-width: 70%;
  padding: 8px 12px;
  border-radius: 18px;
  background: #e3f2fd;
}

.own-message .message-bubble {
  background: #1976d2;
  color: white;
}

.message-input {
  border-top: 1px solid #e0e0e0;
  padding: 16px;
}
</style>