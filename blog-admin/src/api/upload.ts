import request from '@/utils/request'

export interface UploadAsset {
  url: string
  key: string
  originalFilename: string
  contentType: string
  size: number
  type: 'image' | 'file'
}

function buildFormData(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return formData
}

const UPLOAD_TIMEOUT = 120000

export function uploadImage(file: File) {
  return request.post<UploadAsset>('/admin/upload/image', buildFormData(file), {
    timeout: UPLOAD_TIMEOUT
  })
}

export function uploadFile(file: File) {
  return request.post<UploadAsset>('/admin/upload/file', buildFormData(file), {
    timeout: UPLOAD_TIMEOUT
  })
}
