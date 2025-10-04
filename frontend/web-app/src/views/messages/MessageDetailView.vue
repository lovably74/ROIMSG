<template>
  <v-container fluid class="py-6">
    <v-row>
      <v-col cols="12">
        <v-btn
          icon="mdi-arrow-left"
          @click="goBack"
          class="mb-4"
        >
          뒤로
        </v-btn>
        
        <h1 class="text-h4 mb-6">메시지 상세</h1>
        
        <v-card elevation="2" class="pa-6">
          <div v-if="message">
            <v-row>
              <v-col cols="12" md="8">
                <h2 class="text-h5 mb-4">{{ message.subject }}</h2>
                
                <div class="message-meta mb-4">
                  <v-chip class="mr-2">
                    <v-icon start>mdi-account</v-icon>
                    {{ message.sender }}
                  </v-chip>
                  <v-chip class="mr-2">
                    <v-icon start>mdi-clock</v-icon>
                    {{ message.sentAt }}
                  </v-chip>
                  <v-chip v-if="message.readAt">
                    <v-icon start>mdi-check</v-icon>
                    읽음: {{ message.readAt }}
                  </v-chip>
                </div>
                
                <div class="message-content">
                  <p class="text-body-1">{{ message.content }}</p>
                </div>
                
                <div v-if="message.attachments" class="attachments mt-6">
                  <h3 class="text-h6 mb-3">첨부파일</h3>
                  <v-list>
                    <v-list-item
                      v-for="attachment in message.attachments"
                      :key="attachment.id"
                    >
                      <template v-slot:prepend>
                        <v-icon>mdi-paperclip</v-icon>
                      </template>
                      
                      <v-list-item-title>{{ attachment.name }}</v-list-item-title>
                      <v-list-item-subtitle>{{ attachment.size }}</v-list-item-subtitle>
                      
                      <template v-slot:append>
                        <v-btn
                          icon="mdi-download"
                          size="small"
                          @click="downloadAttachment(attachment)"
                        />
                      </template>
                    </v-list-item>
                  </v-list>
                </div>
              </v-col>
              
              <v-col cols="12" md="4">
                <v-card variant="outlined" class="pa-4">
                  <h3 class="text-h6 mb-4">액션</h3>
                  
                  <v-btn
                    color="primary"
                    block
                    class="mb-2"
                    @click="replyMessage"
                  >
                    답장
                  </v-btn>
                  
                  <v-btn
                    color="secondary"
                    block
                    class="mb-2"
                    @click="forwardMessage"
                  >
                    전달
                  </v-btn>
                  
                  <v-btn
                    color="error"
                    variant="outlined"
                    block
                    @click="deleteMessage"
                  >
                    삭제
                  </v-btn>
                </v-card>
              </v-col>
            </v-row>
          </div>
          
          <div v-else class="text-center pa-8">
            <v-progress-circular indeterminate />
            <p class="text-h6 text-medium-emphasis mt-4">메시지를 불러오는 중...</p>
          </div>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const message = ref({
  id: '1',
  subject: '프로젝트 회의 안내',
  content: '안녕하세요. 내일 오후 2시에 프로젝트 회의가 예정되어 있습니다. 회의실 A에서 진행되오니 참석 부탁드립니다.',
  sender: '김철수',
  recipient: '팀 전체',
  sentAt: '2025-01-27 10:30:00',
  readAt: '2025-01-27 10:35:00',
  attachments: [
    {
      id: '1',
      name: '회의자료.pdf',
      size: '2.5 MB'
    }
  ]
})

const goBack = () => {
  router.push('/messages')
}

const replyMessage = () => {
  console.log('답장')
}

const forwardMessage = () => {
  console.log('전달')
}

const deleteMessage = () => {
  console.log('삭제')
}

const downloadAttachment = (attachment: any) => {
  console.log('첨부파일 다운로드:', attachment)
}

onMounted(() => {
  // 메시지 ID로 상세 정보 로드
  const messageId = route.params.id
  console.log('메시지 ID:', messageId)
})
</script>

<style scoped>
.message-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.message-content {
  line-height: 1.6;
  white-space: pre-wrap;
}
</style>