<template>
  <div class="article-edit-page">
    <header class="editor-header">
      <div class="header-left">
        <el-button text @click="router.back()">
          <el-icon><Back /></el-icon>
          返回
        </el-button>
        <div>
          <h1>{{ isEdit ? '编辑文章' : '新建文章' }}</h1>
          <p>{{ form.visibility === 1 ? '私密' : '公开' }} · {{ form.status === 1 ? '已发布' : '草稿' }}</p>
        </div>
      </div>

      <div class="writing-status">
        <span><i :class="{ saved: saveState === 'saved' }"></i>{{ saveStateText }}</span>
        <span>{{ wordCount }} 字</span>
        <span>约 {{ readingMinutes }} 分钟</span>
      </div>

      <div class="header-actions">
        <el-button text @click="focusMode = !focusMode">
          <el-icon><FullScreen /></el-icon>
          {{ focusMode ? '退出专注' : '专注写作' }}
        </el-button>
        <el-button :loading="loading" @click="handleDraftAction">保存草稿</el-button>
        <el-button type="primary" :loading="loading" @click="handleSubmit(1)">
          <el-icon><Check /></el-icon>
          {{ form.status === 1 ? '保存发布' : '发布' }}
        </el-button>
      </div>
    </header>

    <div v-if="draftAvailable" class="draft-recovery">
      <div><strong>发现未提交的本地草稿</strong><span>上次编辑于 {{ draftTimeLabel }}，可恢复到编辑器继续写作。</span></div>
      <div><el-button size="small" @click="discardLocalDraft">忽略</el-button><el-button size="small" type="primary" @click="restoreLocalDraft">恢复草稿</el-button></div>
    </div>

    <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="article-editor-form" :class="{ 'is-focus-mode': focusMode }">
      <main class="writing-pane">
        <el-form-item prop="title" class="title-field">
          <el-input
            v-model="form.title"
            placeholder="写一个清晰、有吸引力的标题…"
            size="large"
            class="title-input"
            maxlength="100"
          />
        </el-form-item>

        <el-form-item prop="content" class="content-field">
          <div
            ref="editorContainerRef"
            class="editor-container"
            @mousedown.capture="handleEditorContainerMouseDown"
            @mouseup="scheduleImageSelectionSync"
            @keyup="scheduleImageSelectionSync"
            @touchend="scheduleImageSelectionSync"
          >
            <Toolbar :editor="editorRef" :defaultConfig="toolbarConfig" mode="default" class="editor-toolbar" />
            <div
              v-if="selectedImage"
              class="image-size-toolbar"
              @mousedown.stop
              @mouseup.stop
              @click.stop
              @keyup.stop
            >
              <div class="image-size-title">
                <el-icon><Picture /></el-icon>
                <span>图片尺寸</span>
                <span class="image-size-name">{{ selectedImage.alt || '已选图片' }}</span>
              </div>
              <div class="image-size-controls">
                <label class="image-size-field">
                  <span>宽</span>
                  <el-input-number
                    v-model="imageSizeForm.width"
                    :min="24"
                    :max="2400"
                    :step="10"
                    size="small"
                    controls-position="right"
                    @change="handleImageWidthChange"
                  />
                  <em>px</em>
                </label>
                <label class="image-size-field">
                  <span>高</span>
                  <el-input-number
                    v-model="imageSizeForm.height"
                    :min="24"
                    :max="2400"
                    :step="10"
                    size="small"
                    controls-position="right"
                    @change="handleImageHeightChange"
                  />
                  <em>px</em>
                </label>
                <el-checkbox v-model="imageSizeForm.lockRatio">按比例联动</el-checkbox>
                <el-button size="small" @click="applyNaturalImageRatio">修正比例</el-button>
                <el-button size="small" @click="resetImageSize">自适应</el-button>
              </div>
            </div>
            <WangEditor
              v-model="form.content"
              :defaultConfig="editorConfig"
              mode="default"
              class="editor"
              @onCreated="handleCreated"
              @onChange="handleEditorChange"
              @onFocus="scheduleImageSelectionSync"
              @customPaste="handleEditorPaste"
            />
          </div>
        </el-form-item>
      </main>

      <aside class="settings-pane">
        <div class="settings-tabs">
          <button type="button" :class="{ active: activeSettingsTab === 'basic' }" @click="activeSettingsTab = 'basic'">发布设置</button>
          <button type="button" :class="{ active: activeSettingsTab === 'media' }" @click="activeSettingsTab = 'media'">素材</button>
          <button type="button" :class="{ active: activeSettingsTab === 'seo' }" @click="activeSettingsTab = 'seo'">摘要与 SEO</button>
        </div>

        <section v-show="activeSettingsTab === 'basic'" class="settings-section">
          <div class="section-title">
            <el-icon><Lock /></el-icon>
            <span>发布</span>
          </div>

          <el-form-item label="状态">
            <el-radio-group v-model="form.status">
              <el-radio-button :label="0">草稿</el-radio-button>
              <el-radio-button :label="1">发布</el-radio-button>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="可见性">
            <el-radio-group v-model="form.visibility">
              <el-radio-button :label="0">公开</el-radio-button>
              <el-radio-button :label="1">私密</el-radio-button>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="置顶">
            <el-switch
              v-model="form.isTop"
              :active-value="1"
              :inactive-value="0"
              active-text="置顶"
              inactive-text="普通"
            />
          </el-form-item>
        </section>

        <section v-show="activeSettingsTab === 'basic'" class="settings-section">
          <div class="section-title">
            <el-icon><CollectionTag /></el-icon>
            <span>归类</span>
          </div>

          <el-form-item label="分类" prop="categoryId">
            <el-select v-model="form.categoryId" placeholder="请选择分类" filterable>
              <el-option
                v-for="item in categories"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="标签">
            <el-select v-model="form.tagIds" multiple placeholder="请选择标签" filterable collapse-tags collapse-tags-tooltip>
              <el-option
                v-for="item in tags"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </section>

        <section v-show="activeSettingsTab === 'media'" class="settings-section">
          <div class="section-title">
            <el-icon><Picture /></el-icon>
            <span>封面</span>
          </div>

          <el-upload
            class="cover-uploader"
            drag
            accept="image/*"
            :show-file-list="false"
            :http-request="handleCoverUpload"
          >
            <img v-if="form.coverImage" :src="form.coverImage" class="cover-preview" alt="cover" />
            <div v-else class="upload-placeholder">
              <el-icon><UploadFilled /></el-icon>
              <span>{{ coverUploading ? '上传中' : '上传封面' }}</span>
            </div>
          </el-upload>
          <el-input v-model="form.coverImage" placeholder="封面图 URL" clearable />
        </section>

        <section v-show="activeSettingsTab === 'media'" class="settings-section">
          <div class="section-title">
            <el-icon><Document /></el-icon>
            <span>附件</span>
          </div>

          <el-upload
            drag
            multiple
            :show-file-list="false"
            :http-request="handleAttachmentUpload"
          >
            <div class="upload-placeholder">
              <el-icon><UploadFilled /></el-icon>
              <span>{{ fileUploading ? '上传中' : '上传文件' }}</span>
            </div>
          </el-upload>

          <div v-if="uploadedFiles.length" class="uploaded-files">
            <div v-for="file in uploadedFiles" :key="file.key" class="uploaded-file">
              <div class="file-meta">
                <span class="file-name">{{ file.originalFilename }}</span>
                <span class="file-size">{{ formatFileSize(file.size) }}</span>
              </div>
              <div class="file-actions">
                <el-tooltip content="插入链接" placement="top">
                  <el-button circle size="small" @click="insertFileLink(file)">
                    <el-icon><Link /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip content="复制链接" placement="top">
                  <el-button circle size="small" @click="copyUrl(file.url)">
                    <el-icon><CopyDocument /></el-icon>
                  </el-button>
                </el-tooltip>
              </div>
            </div>
          </div>
        </section>

        <section v-show="activeSettingsTab === 'seo'" class="settings-section">
          <div class="section-title">
            <el-icon><Memo /></el-icon>
            <span>摘要</span>
          </div>

          <el-form-item label="文章摘要">
            <el-input v-model="form.summary" type="textarea" :rows="6" maxlength="240" show-word-limit placeholder="用两三句话说明这篇文章能帮助读者解决什么问题" />
          </el-form-item>
        </section>

        <section v-show="activeSettingsTab === 'seo'" class="settings-section">
          <div class="section-title">
            <el-icon><View /></el-icon>
            <span>SEO</span>
          </div>

          <el-form-item label="关键词">
            <el-input v-model="form.seoKeywords" placeholder="多个关键词用逗号分隔" />
          </el-form-item>

          <el-form-item label="描述">
            <el-input v-model="form.seoDescription" type="textarea" :rows="3" placeholder="SEO描述" />
          </el-form-item>
        </section>
      </aside>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted, onBeforeUnmount, shallowRef, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import type { UploadRequestOptions } from 'element-plus'
import { ElMessage } from 'element-plus'
import { DomEditor } from '@wangeditor/editor'
import { Editor as WangEditor, Toolbar } from '@wangeditor/editor-for-vue'
import '@wangeditor/editor/dist/css/style.css'
import { Element as SlateElement, Node as SlateNode, Transforms } from 'slate'
import type { Path } from 'slate'
import { getArticle, createArticle, updateArticle } from '@/api/article'
import { getCategoryList } from '@/api/category'
import { getTagList } from '@/api/tag'
import { uploadFile, uploadImage, type UploadAsset } from '@/api/upload'

type ImageStyle = {
  width?: string
  height?: string
}

type ImageNode = {
  type: 'image'
  src: string
  alt?: string
  href?: string
  style?: ImageStyle
}

type CodeBlockNode = {
  type: 'pre'
  children: Array<{
    type: 'code'
    language: string
    children: Array<{ text: string }>
  }>
}

type SelectedImageState = {
  path: Path
  src: string
  alt: string
  width?: number
  height?: number
  naturalWidth?: number
  naturalHeight?: number
  ratio?: number
}

type ResizeDirection = 'left' | 'right' | 'top' | 'bottom'

const router = useRouter()
const route = useRoute()

const formRef = ref()
const editorContainerRef = ref<HTMLElement>()
const loading = ref(false)
const coverUploading = ref(false)
const fileUploading = ref(false)
const categories = ref<any[]>([])
const tags = ref<any[]>([])
const uploadedFiles = ref<UploadAsset[]>([])
const editorRef = shallowRef()
const selectedImage = ref<SelectedImageState | null>(null)
const imageSizeForm = reactive({
  width: undefined as number | undefined,
  height: undefined as number | undefined,
  lockRatio: false
})
const isImageResizing = ref(false)
const focusMode = ref(false)
const activeSettingsTab = ref<'basic' | 'media' | 'seo'>('basic')
const saveState = ref<'idle' | 'saving' | 'saved'>('idle')
const lastSavedAt = ref<Date | null>(null)
const draftAvailable = ref(false)
const pendingDraft = ref<{ form: Record<string, any>; savedAt: string } | null>(null)
const editorReady = ref(false)
let localSaveTimer: ReturnType<typeof setTimeout> | null = null

const isEdit = computed(() => !!route.params.id)
const draftKey = computed(() => `blog-editor-draft:${isEdit.value ? route.params.id : 'new'}`)

const form = reactive({
  title: '',
  slug: '',
  summary: '',
  content: '',
  coverImage: '',
  categoryId: undefined as number | undefined,
  tagIds: [] as number[],
  isTop: 0,
  status: 0,
  visibility: 0,
  seoKeywords: '',
  seoDescription: ''
})

const plainTextContent = computed(() => form.content
  .replace(/<style[^>]*>[\s\S]*?<\/style>/gi, '')
  .replace(/<script[^>]*>[\s\S]*?<\/script>/gi, '')
  .replace(/<[^>]+>/g, ' ')
  .replace(/&nbsp;|&#160;/g, ' ')
  .replace(/\s+/g, '')
)
const wordCount = computed(() => plainTextContent.value.length)
const readingMinutes = computed(() => Math.max(1, Math.ceil(wordCount.value / 500)))
const saveStateText = computed(() => {
  if (saveState.value === 'saving') return '正在保存到本地…'
  if (lastSavedAt.value) return `本地已保存 ${lastSavedAt.value.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })}`
  return '等待输入'
})
const draftTimeLabel = computed(() => pendingDraft.value?.savedAt
  ? new Date(pendingDraft.value.savedAt).toLocaleString('zh-CN', { month: 'numeric', day: 'numeric', hour: '2-digit', minute: '2-digit' })
  : '')

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  content: [{ validator: validateContent }]
}

function validateContent(_rule: unknown, _value: unknown, callback: (error?: Error) => void) {
  const hasMedia = /<(img|video|iframe)\b/i.test(form.content)
  if (!plainTextContent.value && !hasMedia) {
    callback(new Error('请输入正文内容'))
    return
  }
  callback()
}

const toolbarConfig = {
  excludeKeys: ['fullScreen']
}

const editorConfig = {
  placeholder: '从这里开始写作…',
  autoFocus: true,
  // 不使用编辑器内部滚动，正文随内容自动增高，由页面统一滚动（避免双滚动条）
  scroll: false,
  MENU_CONF: {
    uploadImage: {
      customUpload: handleEditorImageUpload
    }
  }
}

function handleCreated(editor: any) {
  editorRef.value = editor
  scheduleImageSelectionSync()
}

function handleEditorChange(editor: any) {
  syncSelectedImageState(editor)
}

function handleEditorPaste(editor: any, event: ClipboardEvent, callback?: (shouldContinueDefaultPaste: boolean) => void) {
  if (DomEditor.getSelectedNodeByType(editor, 'code')) {
    callback?.(true)
    return true
  }

  const clipboardCode = extractClipboardCode(event.clipboardData)
  if (!clipboardCode) {
    callback?.(true)
    return true
  }

  event.preventDefault()
  insertEditorCodeBlock(editor, clipboardCode.code, clipboardCode.language)
  callback?.(false)
  return false
}

function extractClipboardCode(clipboardData: DataTransfer | null) {
  if (!clipboardData) return null

  const html = clipboardData.getData('text/html')
  const plainText = clipboardData.getData('text/plain')
  const htmlCode = extractCodeFromHtml(html)
  if (htmlCode) return htmlCode

  const code = normalizePastedCodeText(plainText)
  if (!code || !isLikelyCodePaste(code)) return null

  return {
    code,
    language: detectPastedCodeLanguage(code)
  }
}

function extractCodeFromHtml(html: string) {
  if (!html || typeof document === 'undefined') return null

  const template = document.createElement('template')
  template.innerHTML = html
  const pre = template.content.querySelector('pre')
  const codeElement = pre?.querySelector('code') || template.content.querySelector('code')
  const sourceElement = pre || codeElement
  if (!sourceElement) return null

  const code = normalizePastedCodeText(sourceElement.textContent || '')
  if (!code || !isLikelyCodePaste(code)) return null

  return {
    code,
    language: getPastedHtmlLanguage(sourceElement, codeElement) || detectPastedCodeLanguage(code)
  }
}

function getPastedHtmlLanguage(sourceElement: Element, codeElement: Element | null) {
  const dataCandidates = [
    codeElement?.getAttribute('data-language'),
    codeElement?.getAttribute('data-lang'),
    sourceElement.getAttribute('data-language'),
    sourceElement.getAttribute('data-lang')
  ]

  for (const candidate of dataCandidates) {
    const language = normalizePastedLanguage(candidate || '')
    if (language) return language
  }

  const classCandidates = [
    codeElement?.getAttribute('class'),
    sourceElement.getAttribute('class')
  ]

  for (const candidate of classCandidates) {
    const language = detectPastedLanguageFromClassName(candidate || '')
    if (language) return language
  }

  return ''
}

function detectPastedLanguageFromClassName(className: string) {
  const rawLanguage = className
    .trim()
    .toLowerCase()
    .match(/(?:language|lang)-([\w+#-]+)|\b(java|javascript|js|typescript|ts|python|py|cpp|c\+\+|csharp|go|rust|sql|bash|shell)\b/i)

  return rawLanguage ? normalizePastedLanguage(rawLanguage[1] || rawLanguage[2]) : ''
}

function normalizePastedLanguage(language: string) {
  const languageName = language.trim().split(/\s+/)[0].toLowerCase()
  const aliases: Record<string, string> = {
    js: 'javascript',
    jsx: 'javascript',
    ts: 'typescript',
    tsx: 'typescript',
    py: 'python',
    sh: 'bash',
    shell: 'bash',
    'c++': 'cpp',
    cplusplus: 'cpp',
    'c#': 'csharp'
  }

  return (aliases[languageName] || languageName).replace(/[^\w-]/g, '')
}

function normalizePastedCodeText(value: string) {
  const lines = value
    .replace(/\r\n?/g, '\n')
    .replace(/\u00a0/g, ' ')
    .split('\n')

  while (lines.length && lines[0].trim() === '') lines.shift()
  while (lines.length && lines[lines.length - 1].trim() === '') lines.pop()

  const indents = lines
    .filter(line => line.trim())
    .map(line => line.match(/^[ \t]*/)?.[0] || '')

  const commonIndent = indents.reduce((common, indent) => {
    let index = 0
    while (index < common.length && common[index] === indent[index]) {
      index++
    }
    return common.slice(0, index)
  }, indents[0] || '')

  return commonIndent
    ? lines.map(line => line.startsWith(commonIndent) ? line.slice(commonIndent.length) : line).join('\n')
    : lines.join('\n')
}

function isLikelyCodePaste(code: string) {
  const lines = code.split('\n')
  const nonEmptyLines = lines.filter(line => line.trim())
  if (nonEmptyLines.length < 2) return false

  const hasIndentedLine = nonEmptyLines.some(line => /^(\t| {2,})\S/.test(line))
  const signalPatterns = [
    /[{};]/,
    /\b(class|public|private|protected|static|void|return|if|else|for|while|function|const|let|var|def|import|from|package|interface|extends|new|vector|map|hashmap)\b/i,
    /=>|==|!=|<=|>=|\+\+|--|&&|\|\||::/,
    /#include|System\.out|console\.log|fmt\.Println/
  ]
  const signalCount = signalPatterns.filter(pattern => pattern.test(code)).length

  return signalCount >= 2 || (hasIndentedLine && signalCount >= 1)
}

function detectPastedCodeLanguage(code: string) {
  if (/#include|std::|vector<|public:\s*$|private:\s*$/m.test(code)) return 'cpp'
  if (/System\.out|public\s+(?:class|interface)|\bint\[\]|\bHashMap\b|\bList<|\bString\[\]/.test(code)) return 'java'
  if (/\bdef\s+\w+\(|\bfrom\s+\w+\s+import\b|\bList\[|\bNone\b/.test(code)) return 'python'
  if (/\binterface\s+\w+|:\s*(string|number|boolean)\b|Array<\w+>/.test(code)) return 'typescript'
  if (/\bfunction\b|\bconst\b|\blet\b|=>|console\.log/.test(code)) return 'javascript'
  if (/\bfunc\s+\w+\(|fmt\.Println|:=/.test(code)) return 'go'
  if (/\bSELECT\b|\bINSERT\b|\bUPDATE\b|\bDELETE\b|\bFROM\b/i.test(code)) return 'sql'
  return ''
}

function insertEditorCodeBlock(editor: any, code: string, language: string) {
  const codeBlock: CodeBlockNode = {
    type: 'pre',
    children: [
      {
        type: 'code',
        language,
        children: [{ text: code }]
      }
    ]
  }

  Transforms.insertNodes(editor, [
    codeBlock,
    { type: 'paragraph', children: [{ text: '' }] }
  ] as any, { mode: 'highest' })
  editor.focus?.()
}

watch(form, () => {
  if (!editorReady.value) return
  saveState.value = 'saving'
  if (localSaveTimer) window.clearTimeout(localSaveTimer)
  localSaveTimer = window.setTimeout(saveLocalDraft, 700)
}, { deep: true })

onMounted(async () => {
  await Promise.all([fetchCategories(), fetchTags()])
  if (isEdit.value) await fetchArticle()
  detectLocalDraft()
  editorReady.value = true
  window.addEventListener('keydown', handleEditorShortcut)
})

onBeforeUnmount(() => {
  if (localSaveTimer) window.clearTimeout(localSaveTimer)
  window.removeEventListener('keydown', handleEditorShortcut)
  removeImageResizeHandles()
  const editor = editorRef.value
  if (editor) {
    editor.destroy()
  }
})

function saveLocalDraft() {
  const snapshot = {
    form: JSON.parse(JSON.stringify(form)),
    savedAt: new Date().toISOString()
  }
  localStorage.setItem(draftKey.value, JSON.stringify(snapshot))
  lastSavedAt.value = new Date(snapshot.savedAt)
  saveState.value = 'saved'
}

function detectLocalDraft() {
  try {
    const raw = localStorage.getItem(draftKey.value)
    if (!raw) return
    const snapshot = JSON.parse(raw)
    if (!snapshot?.form || !snapshot?.savedAt) return
    pendingDraft.value = snapshot
    const currentContent = `${form.title}${form.content}`.trim()
    const draftContent = `${snapshot.form.title || ''}${snapshot.form.content || ''}`.trim()
    draftAvailable.value = Boolean(draftContent && draftContent !== currentContent)
  } catch (error) {
    console.warn('读取本地草稿失败', error)
  }
}

function restoreLocalDraft() {
  if (!pendingDraft.value?.form) return
  Object.assign(form, pendingDraft.value.form)
  draftAvailable.value = false
  lastSavedAt.value = new Date(pendingDraft.value.savedAt)
  saveState.value = 'saved'
  ElMessage.success('已恢复本地草稿')
}

function discardLocalDraft() {
  localStorage.removeItem(draftKey.value)
  pendingDraft.value = null
  draftAvailable.value = false
}

function handleEditorShortcut(event: KeyboardEvent) {
  if ((event.metaKey || event.ctrlKey) && event.key.toLowerCase() === 's') {
    event.preventDefault()
    handleDraftAction()
  }
}

function handleDraftAction() {
  if (isEdit.value) {
    handleSubmit(0, true)
    return
  }
  saveLocalDraft()
  ElMessage.success('草稿已保存在当前浏览器')
}

function scheduleImageSelectionSync() {
  window.setTimeout(() => syncSelectedImageState(), 0)
}

function syncSelectedImageState(editor = editorRef.value) {
  if (!editor || isImageResizing.value) return

  const entry = getSelectedImageEntry(editor)
  if (!entry) {
    if (isImageSizeToolbarFocused()) {
      nextTick(mountImageResizeHandles)
      return
    }
    selectedImage.value = null
    removeImageResizeHandles()
    return
  }

  const { node, path } = entry
  const container = getSelectedImageContainer()
  const image = container?.querySelector('img') as HTMLImageElement | null
  const rect = container?.getBoundingClientRect()
  const width = parseCssSize(node.style?.width) ?? safeRound(rect?.width)
  const height = parseCssSize(node.style?.height) ?? safeRound(rect?.height)
  const naturalWidth = safeRound(image?.naturalWidth)
  const naturalHeight = safeRound(image?.naturalHeight)
  const ratio = naturalWidth && naturalHeight
    ? naturalWidth / naturalHeight
    : width && height
      ? width / height
      : undefined

  selectedImage.value = {
    path,
    src: node.src,
    alt: node.alt || '',
    width,
    height,
    naturalWidth,
    naturalHeight,
    ratio
  }
  imageSizeForm.width = width
  imageSizeForm.height = height

  nextTick(mountImageResizeHandles)
}

function getSelectedImageEntry(editor = editorRef.value): { node: ImageNode; path: Path } | null {
  if (!editor) return null
  const node = DomEditor.getSelectedNodeByType(editor, 'image') as ImageNode | null
  if (!node) return null
  const path = DomEditor.findPath(editor, node)
  return { node, path }
}

function getImageNodeAtPath(path: Path): ImageNode | null {
  const editor = editorRef.value
  if (!editor) return null

  try {
    const node = SlateNode.get(editor, path)
    if (!SlateElement.isElement(node) || (node as any).type !== 'image') return null
    return node as ImageNode
  } catch {
    return null
  }
}

function updateSelectedImageStyle(nextStyle: ImageStyle, replace = false) {
  const editor = editorRef.value
  const image = selectedImage.value
  if (!editor || !image) return

  const currentNode = getImageNodeAtPath(image.path)
  if (!currentNode) {
    scheduleImageSelectionSync()
    return
  }

  const style = replace ? { ...nextStyle } : { ...(currentNode.style || {}), ...nextStyle }
  Object.keys(style).forEach(key => {
    const styleKey = key as keyof ImageStyle
    if (!style[styleKey]) {
      delete style[styleKey]
    }
  })

  Transforms.setNodes(editor, { style }, { at: image.path })
  selectedImage.value = {
    ...image,
    width: parseCssSize(style.width),
    height: parseCssSize(style.height)
  }
  nextTick(mountImageResizeHandles)
}

function handleImageWidthChange(value: number | undefined) {
  const width = normalizeImageSize(value)
  if (!width) return

  let height = normalizeImageSize(imageSizeForm.height) ?? selectedImage.value?.height
  if (imageSizeForm.lockRatio && selectedImage.value?.ratio) {
    height = Math.max(24, Math.round(width / selectedImage.value.ratio))
    imageSizeForm.height = height
  }

  applyImageDimensions(width, height)
}

function handleImageHeightChange(value: number | undefined) {
  const height = normalizeImageSize(value)
  if (!height) return

  let width = normalizeImageSize(imageSizeForm.width) ?? selectedImage.value?.width
  if (imageSizeForm.lockRatio && selectedImage.value?.ratio) {
    width = Math.max(24, Math.round(height * selectedImage.value.ratio))
    imageSizeForm.width = width
  }

  applyImageDimensions(width, height)
}

function applyImageDimensions(width?: number, height?: number) {
  const normalizedWidth = normalizeImageSize(width)
  const normalizedHeight = normalizeImageSize(height)
  if (!normalizedWidth && !normalizedHeight) return

  imageSizeForm.width = normalizedWidth
  imageSizeForm.height = normalizedHeight
  updateSelectedImageStyle({
    width: normalizedWidth ? `${normalizedWidth}px` : undefined,
    height: normalizedHeight ? `${normalizedHeight}px` : undefined
  })
}

function applyNaturalImageRatio() {
  const image = selectedImage.value
  if (!image?.ratio) return

  const width = normalizeImageSize(imageSizeForm.width) ?? image.width ?? image.naturalWidth
  if (!width) return

  const height = Math.max(24, Math.round(width / image.ratio))
  imageSizeForm.width = width
  imageSizeForm.height = height
  applyImageDimensions(width, height)
}

function resetImageSize() {
  imageSizeForm.width = undefined
  imageSizeForm.height = undefined
  updateSelectedImageStyle({}, true)
}

function handleEditorContainerMouseDown(event: MouseEvent) {
  const target = event.target as HTMLElement | null
  const handle = target?.closest<HTMLElement>('.image-side-resizer')
  if (!handle) return

  const direction = handle.dataset.direction as ResizeDirection | undefined
  if (!direction) return
  startImageSideResize(event, direction)
}

function startImageSideResize(event: MouseEvent, direction: ResizeDirection) {
  const container = getSelectedImageContainer()
  const image = selectedImage.value
  if (!container || !image) return

  event.preventDefault()
  event.stopPropagation()

  const img = container.querySelector('img') as HTMLImageElement | null
  const rect = container.getBoundingClientRect()
  const startX = event.clientX
  const startY = event.clientY
  const startWidth = Math.max(24, rect.width)
  const startHeight = Math.max(24, rect.height)
  let nextWidth = startWidth
  let nextHeight = startHeight

  isImageResizing.value = true
  document.body.style.cursor = direction === 'left' || direction === 'right' ? 'ew-resize' : 'ns-resize'

  const move = (moveEvent: MouseEvent) => {
    moveEvent.preventDefault()
    const deltaX = moveEvent.clientX - startX
    const deltaY = moveEvent.clientY - startY

    if (direction === 'right') {
      nextWidth = startWidth + deltaX
      nextHeight = startHeight
    } else if (direction === 'left') {
      nextWidth = startWidth - deltaX
      nextHeight = startHeight
    } else if (direction === 'bottom') {
      nextWidth = startWidth
      nextHeight = startHeight + deltaY
    } else {
      nextWidth = startWidth
      nextHeight = startHeight - deltaY
    }

    if (imageSizeForm.lockRatio && image.ratio) {
      if (direction === 'left' || direction === 'right') {
        nextHeight = nextWidth / image.ratio
      } else {
        nextWidth = nextHeight * image.ratio
      }
    }

    nextWidth = Math.max(24, nextWidth)
    nextHeight = Math.max(24, nextHeight)
    container.style.width = `${Math.round(nextWidth)}px`
    container.style.height = `${Math.round(nextHeight)}px`
    if (img) {
      img.style.width = '100%'
      img.style.height = '100%'
    }
  }

  const up = () => {
    document.removeEventListener('mousemove', move)
    document.removeEventListener('mouseup', up)
    document.body.style.cursor = ''
    isImageResizing.value = false
    applyImageDimensions(Math.round(nextWidth), Math.round(nextHeight))
    scheduleImageSelectionSync()
  }

  document.addEventListener('mousemove', move)
  document.addEventListener('mouseup', up)
}

function getSelectedImageContainer() {
  return editorContainerRef.value?.querySelector<HTMLElement>('.w-e-selected-image-container') || null
}

function mountImageResizeHandles() {
  removeImageResizeHandles()
  if (!selectedImage.value) return

  const container = getSelectedImageContainer()
  if (!container) return

  container.classList.add('custom-image-resize-enabled')
  ;(['left', 'right', 'top', 'bottom'] as ResizeDirection[]).forEach(direction => {
    const handle = document.createElement('span')
    handle.className = `image-side-resizer image-side-resizer--${direction}`
    handle.dataset.direction = direction
    handle.title = direction === 'left' || direction === 'right' ? '横向调整图片' : '纵向调整图片'
    container.appendChild(handle)
  })
}

function removeImageResizeHandles() {
  editorContainerRef.value?.querySelectorAll('.image-side-resizer').forEach(handle => handle.remove())
  editorContainerRef.value
    ?.querySelectorAll('.custom-image-resize-enabled')
    .forEach(container => container.classList.remove('custom-image-resize-enabled'))
}

function isImageSizeToolbarFocused() {
  const activeElement = document.activeElement as HTMLElement | null
  return Boolean(selectedImage.value && activeElement?.closest('.image-size-toolbar'))
}

function parseCssSize(value?: string) {
  if (!value) return undefined
  const size = Number.parseFloat(value)
  return Number.isFinite(size) ? Math.round(size) : undefined
}

function safeRound(value?: number) {
  return typeof value === 'number' && Number.isFinite(value) && value > 0 ? Math.round(value) : undefined
}

function normalizeImageSize(value?: number) {
  if (typeof value !== 'number' || !Number.isFinite(value)) return undefined
  return Math.max(24, Math.round(value))
}

async function fetchCategories() {
  try {
    const res = await getCategoryList()
    categories.value = res.data
  } catch (error) {
    console.error('获取分类失败', error)
  }
}

async function fetchTags() {
  try {
    const res = await getTagList()
    tags.value = res.data
  } catch (error) {
    console.error('获取标签失败', error)
  }
}

async function fetchArticle() {
  try {
    const res = await getArticle(Number(route.params.id))
    Object.assign(form, {
      ...res.data,
      visibility: res.data.visibility ?? 0
    })
    form.tagIds = res.data.tags?.map((t: any) => t.id) || []
  } catch (error) {
    console.error('获取文章失败', error)
  }
}

async function handleSubmit(nextStatus?: number, stayOnPage = false) {
  if (typeof nextStatus === 'number') {
    form.status = nextStatus
  }

  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) {
    ElMessage.warning('请先补全标题、分类和正文')
    return
  }

  loading.value = true
  try {
    if (isEdit.value) {
      await updateArticle(Number(route.params.id), form)
    } else {
      await createArticle(form)
    }
    if (localSaveTimer) window.clearTimeout(localSaveTimer)
    localStorage.removeItem(draftKey.value)
    lastSavedAt.value = new Date()
    saveState.value = 'saved'
    ElMessage.success(nextStatus === 1 ? '文章已发布' : '草稿已保存')
    if (!stayOnPage) router.push('/article')
  } catch (error) {
    console.error('保存失败', error)
  } finally {
    loading.value = false
  }
}

async function handleEditorImageUpload(file: File, insertFn: (url: string, alt: string, href: string) => void) {
  const res = await uploadImage(file)
  insertFn(res.data.url, res.data.originalFilename, res.data.url)
}

async function handleCoverUpload(options: UploadRequestOptions) {
  coverUploading.value = true
  try {
    const res = await uploadImage(options.file as File)
    form.coverImage = res.data.url
    options.onSuccess?.(res.data)
    ElMessage.success('封面已上传')
  } catch (error) {
    options.onError?.(error as Error)
  } finally {
    coverUploading.value = false
  }
}

async function handleAttachmentUpload(options: UploadRequestOptions) {
  fileUploading.value = true
  try {
    const res = await uploadFile(options.file as File)
    uploadedFiles.value.unshift(res.data)
    insertFileLink(res.data)
    options.onSuccess?.(res.data)
    ElMessage.success('文件已上传')
  } catch (error) {
    options.onError?.(error as Error)
  } finally {
    fileUploading.value = false
  }
}

function insertFileLink(file: UploadAsset) {
  const name = escapeHtml(file.originalFilename || file.url)
  const url = escapeAttribute(file.url)
  const html = `<p><a href="${url}" target="_blank" rel="noopener noreferrer">${name}</a></p>`
  if (editorRef.value?.dangerouslyInsertHtml) {
    editorRef.value.dangerouslyInsertHtml(html)
  } else {
    form.content += html
  }
}

async function copyUrl(url: string) {
  await navigator.clipboard?.writeText(url)
  ElMessage.success('链接已复制')
}

function formatFileSize(size: number) {
  if (!size) return '0 B'
  if (size < 1024) return `${size} B`
  if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)} KB`
  return `${(size / 1024 / 1024).toFixed(1)} MB`
}

function escapeHtml(value: string) {
  return value
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')
}

function escapeAttribute(value: string) {
  return escapeHtml(value).replace(/`/g, '&#96;')
}
</script>

<style scoped lang="scss">
.article-edit-page {
  min-height: 100vh;
  background: #f3f4f1;
  color: #26302a;
}

.editor-header {
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  min-height: 68px;
  padding: 12px 24px;
  background: rgba(255, 255, 255, .94);
  border-bottom: 1px solid #dfe4df;
  backdrop-filter: blur(16px);
}

.writing-status {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 18px;
  color: #87908a;
  font-size: 12px;

  span { display: inline-flex; align-items: center; gap: 6px; }

  i {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: #d5a84a;

    &.saved { background: #5b9b71; }
  }
}

.draft-recovery {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  margin: 16px 24px 0;
  padding: 12px 16px;
  border: 1px solid #d9c99e;
  border-radius: 10px;
  background: #fffaf0;

  > div:first-child { display: flex; flex-direction: column; gap: 2px; }
  strong { color: #5f4c23; font-size: 13px; }
  span { color: #8a7952; font-size: 12px; }
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;

  h1 {
    margin: 0;
    font-size: 18px;
    line-height: 1.3;
    color: #1f2937;
  }

  p {
    margin: 2px 0 0;
    font-size: 12px;
    color: #909399;
  }
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.article-editor-form {
  display: grid;
  grid-template-columns: minmax(0, 960px) 340px;
  justify-content: center;
  gap: 20px;
  margin: 0 auto;
  padding: 20px 24px 28px;
  align-items: start;

  &.is-focus-mode {
    display: block;
    max-width: 1008px;

    .settings-pane { display: none; }
  }
}

.writing-pane,
.settings-pane {
  min-width: 0;
}

.writing-pane {
  background: #fff;
  border: 1px solid #dfe4df;
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(44, 56, 48, .04);
}

.title-field {
  margin: 0;
  padding: 24px 32px 16px;
  border-bottom: 1px solid #ebeef5;

  :deep(.el-form-item__content) {
    display: block;
  }

  :deep(.el-input__wrapper) {
    box-shadow: none;
    padding: 0;
  }

  :deep(.el-input__inner) {
    height: 48px;
    font-size: 26px;
    font-weight: 650;
    letter-spacing: 0;
    color: #1f2923;
  }
}

.content-field {
  margin: 0;

  :deep(.el-form-item__content) {
    display: block;
  }
}

.editor-container {
  width: 100%;
}

.editor-toolbar {
  position: sticky;
  top: 68px;
  z-index: 5;
  border-bottom: 1px solid #ebeef5;
  background: rgba(255, 255, 255, .96);
  backdrop-filter: blur(10px);

  :deep(.w-e-bar) {
    padding: 4px 24px;
    background: transparent;
  }
}

.image-size-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 10px 12px;
  border-bottom: 1px solid #ebeef5;
  background: #f9fafb;
}

.image-size-title {
  min-width: 0;
  display: flex;
  align-items: center;
  gap: 6px;
  color: #303133;
  font-size: 13px;
  font-weight: 700;
}

.image-size-name {
  min-width: 0;
  max-width: 180px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #909399;
  font-weight: 400;
}

.image-size-controls {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  flex-wrap: wrap;
  gap: 8px;
}

.image-size-field {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #606266;

  :deep(.el-input-number) {
    width: 116px;
  }

  em {
    font-style: normal;
    color: #909399;
  }
}

.editor-container {
  :deep(.w-e-text-container [data-slate-editor] pre) {
    margin: 16px 0;
    max-width: 100%;
    overflow: hidden;
    border: 1px solid #dcdfe6;
    border-radius: 8px;
    background: #f8fafc;
  }

  :deep(.w-e-text-container [data-slate-editor] pre > code) {
    display: block;
    max-width: 100%;
    padding: 16px;
    overflow-x: auto;
    color: #24292f;
    background: transparent;
    border: 0;
    border-radius: 0;
    font-family: "JetBrains Mono", "Fira Code", Consolas, monospace;
    font-size: 14px;
    line-height: 1.65;
    white-space: pre;
    word-break: normal;
    overflow-wrap: normal;
    tab-size: 4;
  }

  :deep(.w-e-image-container) {
    max-width: 100%;
  }

  :deep(.w-e-image-container > img) {
    max-width: 100%;
  }

  :deep(.custom-image-resize-enabled) {
    position: relative;
    overflow: visible !important;
    outline: 2px solid #409eff;
    outline-offset: 2px;
  }

  :deep(.image-side-resizer) {
    position: absolute;
    z-index: 4;
    display: block;
    width: 10px;
    height: 10px;
    border: 2px solid #fff;
    border-radius: 50%;
    background: #409eff;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.2);
  }

  :deep(.image-side-resizer--left),
  :deep(.image-side-resizer--right) {
    top: 50%;
    transform: translateY(-50%);
    cursor: ew-resize;
  }

  :deep(.image-side-resizer--left) {
    left: -7px;
  }

  :deep(.image-side-resizer--right) {
    right: -7px;
  }

  :deep(.image-side-resizer--top),
  :deep(.image-side-resizer--bottom) {
    left: 50%;
    transform: translateX(-50%);
    cursor: ns-resize;
  }

  :deep(.image-side-resizer--top) {
    top: -7px;
  }

  :deep(.image-side-resizer--bottom) {
    bottom: -7px;
  }
}

/*
 * 正文区：文字从左上角顶格开始，与标题左边缘对齐。
 * 之前 [data-slate-editor] 被 max-width + margin:auto 居中，再加大号内边距，
 * 光标会落在编辑区中间，而占位提示仍停在左上角，看起来"输入位置不对"。
 */
.editor {
  :deep(.w-e-text-container) {
    height: auto;
  }

  :deep(.w-e-text-container [data-slate-editor]) {
    min-height: calc(100vh - 300px);
    margin: 0;
    padding: 20px 32px 96px;
    color: #303b34;
    font-size: 16px;
    line-height: 1.85;
    text-align: left;
    cursor: text;
  }

  :deep(.w-e-text-container [data-slate-editor] p) {
    margin: 0 0 .9em;
  }

  :deep(.w-e-text-container [data-slate-editor] h1),
  :deep(.w-e-text-container [data-slate-editor] h2),
  :deep(.w-e-text-container [data-slate-editor] h3),
  :deep(.w-e-text-container [data-slate-editor] h4) {
    margin: 1.4em 0 .6em;
    color: #1f2923;
    font-weight: 650;
    line-height: 1.45;
  }

  :deep(.w-e-text-container [data-slate-editor] h1) { font-size: 26px; }
  :deep(.w-e-text-container [data-slate-editor] h2) { font-size: 22px; }
  :deep(.w-e-text-container [data-slate-editor] h3) { font-size: 19px; }
  :deep(.w-e-text-container [data-slate-editor] h4) { font-size: 17px; }

  :deep(.w-e-text-container [data-slate-editor] ul),
  :deep(.w-e-text-container [data-slate-editor] ol) {
    margin: 0 0 .9em;
    padding-left: 1.6em;
  }

  :deep(.w-e-text-container [data-slate-editor] blockquote) {
    margin: 0 0 .9em;
    padding: 8px 16px;
    border-left: 3px solid #486955;
    background: #f4f7f5;
    color: #59615b;
  }

  /* 占位提示与第一行文字位置完全重合 */
  :deep(.w-e-text-placeholder) {
    top: 20px;
    left: 32px;
    width: calc(100% - 64px);
    color: #a9b1ab;
    font-size: 16px;
    font-style: normal;
    line-height: 1.85;
  }
}

.settings-pane {
  position: sticky;
  top: 88px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: calc(100vh - 96px);
  overflow-y: auto;
}

.settings-tabs {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  padding: 4px;
  border: 1px solid #dfe4df;
  border-radius: 10px;
  background: #e9ece9;

  button {
    padding: 8px 4px;
    border-radius: 7px;
    color: #747e77;
    font-size: 12px;
    transition: all .2s ease;

    &.active {
      color: #315640;
      background: #fff;
      box-shadow: 0 1px 4px rgba(31, 45, 36, .08);
      font-weight: 600;
    }
  }
}

.settings-section {
  padding: 18px;
  background: #fff;
  border: 1px solid #dfe4df;
  border-radius: 10px;

  :deep(.el-form-item) {
    margin-bottom: 16px;
  }

  :deep(.el-form-item:last-child) {
    margin-bottom: 0;
  }

  :deep(.el-select),
  :deep(.el-radio-group) {
    width: 100%;
  }

  :deep(.el-radio-button) {
    flex: 1;
  }

  :deep(.el-radio-button__inner) {
    width: 100%;
  }
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 14px;
  font-size: 14px;
  font-weight: 700;
  color: #303133;
}

.cover-uploader {
  margin-bottom: 10px;
}

.cover-preview {
  display: block;
  width: 100%;
  aspect-ratio: 16 / 9;
  object-fit: cover;
}

.upload-placeholder {
  min-height: 92px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #909399;
  font-size: 13px;

  .el-icon {
    font-size: 24px;
  }
}

.uploaded-files {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 12px;
}

.uploaded-file {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  padding: 10px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  background: #fafafa;
}

.file-meta {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.file-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 13px;
  color: #303133;
}

.file-size {
  font-size: 12px;
  color: #909399;
}

.file-actions {
  display: flex;
  gap: 6px;
  flex-shrink: 0;
}

@media (max-width: 1180px) {
  .article-editor-form {
    grid-template-columns: minmax(0, 1fr);
  }

  .settings-pane {
    position: static;
    max-height: none;
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .editor :deep(.w-e-text-container [data-slate-editor]) {
    min-height: 520px;
  }
}

@media (max-width: 760px) {
  .writing-status {
    display: none;
  }

  .draft-recovery {
    align-items: flex-start;
    flex-direction: column;
    margin: 12px 12px 0;
  }

  .editor-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .header-actions {
    width: 100%;

    .el-button {
      flex: 1;
    }
  }

  .article-editor-form {
    padding: 12px;
  }

  .settings-pane {
    grid-template-columns: 1fr;
  }

  .title-field :deep(.el-input__inner) {
    font-size: 22px;
  }

  .title-field {
    padding: 18px 20px 12px;
  }

  .editor-toolbar :deep(.w-e-bar) {
    padding: 4px 12px;
  }

  .editor :deep(.w-e-text-container [data-slate-editor]) {
    padding: 16px 20px 80px;
  }

  .editor :deep(.w-e-text-placeholder) {
    top: 16px;
    left: 20px;
    width: calc(100% - 40px);
  }
}
</style>
