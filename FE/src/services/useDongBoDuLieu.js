import { useSyncExternalStore } from 'react'
import { docPhienDongBo, theoDoiDongBo } from './dongBoDuLieu.js'

const docPhienBanBanDau = () => 0

export function useDongBoDuLieu() {
  return useSyncExternalStore(theoDoiDongBo, docPhienDongBo, docPhienBanBanDau)
}
