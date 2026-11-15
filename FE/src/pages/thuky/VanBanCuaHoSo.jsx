import { useEffect, useState } from 'react'
import { layBienBanTheoCuocHop } from '../../api/bienBanApi.js'
import { layKetLuanTheoCuocHop } from '../../api/ketLuanCuocHopApi.js'
import TaiLieuKetLuan from './TaiLieuKetLuan.jsx'

export default function VanBanCuaHoSo({ loai, cuocHopId, token }) {
  const [vanBan, setVanBan] = useState(undefined)
  const [loi, setLoi] = useState('')
  const laKetLuan = loai === 'ket-luan'
  useEffect(() => {
    const controller = new AbortController()
    const tai = laKetLuan ? layKetLuanTheoCuocHop : layBienBanTheoCuocHop
    tai(cuocHopId, token, controller.signal)
      .then((duLieu) => { if (!controller.signal.aborted) setVanBan(duLieu) })
      .catch((error) => { if (!controller.signal.aborted) setLoi(error.message) })
    return () => controller.abort()
  }, [cuocHopId, token, laKetLuan])

  if (loi) return <p role="alert" className="text-sm text-red-800">{loi}</p>
  if (vanBan === undefined) return <p role="status" className="text-sm">Đang tải văn bản…</p>
  if (!vanBan) return <p className="text-sm">Chưa có văn bản cho cuộc họp này.</p>
  return (
    <article className="space-y-4 rounded-xl border border-slate-200 p-4">
      <h3 className="font-semibold">{vanBan.tieuDe}</h3>
      <p className="whitespace-pre-wrap break-words text-sm leading-6">
        {vanBan.noiDungChinhThuc || vanBan.noiDung || vanBan.noiDungGoc || 'Chưa có nội dung văn bản.'}
      </p>
      {laKetLuan && <TaiLieuKetLuan cuocHopId={cuocHopId} token={token} choPhepTaiLen={false} />}
    </article>
  )
}
