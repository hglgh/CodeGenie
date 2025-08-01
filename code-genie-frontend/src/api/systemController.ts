// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 此处后端没有提供注释 GET /system/health */
export async function health(options?: { [key: string]: any }) {
  return request<string>('/system/health', {
    method: 'GET',
    ...(options || {}),
  })
}
