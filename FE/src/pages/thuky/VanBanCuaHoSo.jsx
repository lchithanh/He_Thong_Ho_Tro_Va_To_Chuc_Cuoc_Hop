import { useEffect, useState } from 'react'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import {
  layBienBanChoToiDuyet,
  layBienBanTheoCuocHop,
  phatHanhBienBan,
  xuLyDuyetBienBan,
} from '../../api/bienBanApi.js'
import { layKetLuanTheoCuocHop, phatHanhKetLuan, xuLyDuyetKetLuan } from '../../api/ketLuanCuocHopApi.js'
import TaiLieuKetLuan from './TaiLieuKetLuan.jsx'
import BanXemTruocVanBan from './BanXemTruocVanBan.jsx'
import KhongGianVanBan from './KhongGianVanBan.jsx'

const TEN_TRANG_THAI = {
  NHAP: 'Bản nháp', CHO_DUYET: 'Chờ duyệt', DA_DUYET: 'Đã duyệt',
  DA_PHE_DUYET: 'Đã duyệt', BI_TRA_LAI: 'Bị trả lại', DA_PHAT_HANH: 'Đã phát hành',
}

export default function VanBanCuaHoSo({ loai, cuocHopId, token, choPhepXuLy = false, onPhatHanh, tenChuTri }) {
  const phienDongBo = useDongBoDuLieu()
  const [vanBan, setVanBan] = useState(undefined)
  const [loi, setLoi] = useState('')
  const [dangXuLy, setDangXuLy] = useState(false)
  const [loiXuLy, setLoiXuLy] = useState('')
  const [thongBao, setThongBao] = useState('')
  const laKetLuan = loai === 'ket-luan'
  const coTheDuyet = choPhepXuLy && vanBan?.trangThai === 'CHO_DUYET'
  const coThePhatHanh = choPhepXuLy
    && vanBan?.trangThai === (laKetLuan ? 'DA_DUYET' : 'DA_PHE_DUYET')

  async function xuLyDuyet(event) {
    event.preventDefault()
    if (!coTheDuyet || dangXuLy) return

    const thaoTac = event.nativeEvent.submitter?.value
    if (thaoTac !== 'phe-duyet' && thaoTac !== 'tra-lai') return
    const yKien = new FormData(event.currentTarget).get('yKien').trim()
    setThongBao('')
    if (thaoTac === 'tra-lai' && !yKien) {
      setLoiXuLy('Nhập lý do trả lại để thư ký biết nội dung cần sửa.')
      return
    }

    setDangXuLy(true)
    setLoiXuLy('')
    try {
      const yeuCau = laKetLuan ? vanBan : (await layBienBanChoToiDuyet(token))
        .find((muc) => muc.bienBanId === vanBan.bienBanId && muc.cuocHopId === vanBan.cuocHopId)
      if (yeuCau?.yeuCauPheDuyetId == null) {
        setLoiXuLy('Không tìm thấy yêu cầu đang chờ bạn duyệt. Hãy mở lại hồ sơ để cập nhật.')
        return
      }
      const guiQuyetDinh = laKetLuan ? xuLyDuyetKetLuan : xuLyDuyetBienBan
      const ketQua = await guiQuyetDinh(yeuCau, thaoTac, yKien || null, token)
      setVanBan((hienTai) => ({ ...hienTai, trangThai: ketQua.trangThai }))
      setThongBao(ketQua.message)
    } catch (error) {
      setLoiXuLy(error.message)
    } finally {
      setDangXuLy(false)
    }
  }

  async function xuLyPhatHanh() {
    if (!coThePhatHanh || dangXuLy) return
    setDangXuLy(true)
    setLoiXuLy('')
    setThongBao('')
    try {
      const phatHanh = laKetLuan ? phatHanhKetLuan : phatHanhBienBan
      setVanBan(await phatHanh(cuocHopId, token))
      onPhatHanh?.()
    } catch (error) {
      setLoiXuLy(error.message)
    } finally {
      setDangXuLy(false)
    }
  }
  useEffect(() => {
    const controller = new AbortController()
    const tai = laKetLuan ? layKetLuanTheoCuocHop : layBienBanTheoCuocHop
    tai(cuocHopId, token, controller.signal)
      .then((duLieu) => { if (!controller.signal.aborted) setVanBan(duLieu) })
      .catch((error) => { if (!controller.signal.aborted) setLoi(error.message) })
    return () => controller.abort()
  }, [cuocHopId, token, laKetLuan, phienDongBo])

  if (loi) return <p role="alert" className="text-sm text-red-800">{loi}</p>
  if (vanBan === undefined) return <p role="status" className="text-sm">Đang tải văn bản…</p>
  if (!vanBan) return <p className="text-sm">Chưa có văn bản cho cuộc họp này.</p>
  return (
    <KhongGianVanBan banXem={(
      <BanXemTruocVanBan
        tenLoai={laKetLuan ? 'Kết luận cuộc họp' : 'Biên bản cuộc họp'}
        tieuDe={vanBan.tieuDe}
        trangThai={TEN_TRANG_THAI[vanBan.trangThai] || vanBan.trangThai}
        tenNguoiKy={tenChuTri}
        ngayPhatHanh={vanBan.ngayPhatHanh}
        noiDung={vanBan.noiDungChinhThuc || vanBan.noiDung || vanBan.noiDungGoc}
        thongTin={[
          ['Cuộc họp', vanBan.tieuDeCuocHop],
          ['Người soạn', vanBan.tenNguoiSoan],
          ['Người duyệt', vanBan.tenNguoiDuyet],
          ['Người phát hành', vanBan.tenNguoiPhatHanh],
          ['Ngày phát hành', vanBan.ngayPhatHanh && new Date(vanBan.ngayPhatHanh).toLocaleString('vi-VN')],
        ].filter(([, giaTri]) => giaTri)}
      />
    )}>
      <TaiLieuKetLuan loai={loai} cuocHopId={cuocHopId} token={token} />
      {loiXuLy && <p role="alert" className="text-sm text-red-800">{loiXuLy}</p>}
      {thongBao && <p role="status" className="text-sm text-emerald-800">{thongBao}</p>}
      {coTheDuyet && (
        <form onSubmit={xuLyDuyet} className="space-y-3 border-t border-slate-200 pt-4">
          <label className="block text-sm font-medium">
            Ý kiến xử lý
            <textarea name="yKien" rows={2} disabled={dangXuLy}
              className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2" />
          </label>
          <div className="flex flex-wrap gap-2">
            <button type="submit" value="phe-duyet" disabled={dangXuLy}
              className="rounded-lg bg-emerald-800 px-4 py-2 text-sm font-semibold text-white disabled:opacity-60">
              {dangXuLy ? 'Đang xử lý…' : laKetLuan ? 'Duyệt kết luận' : 'Duyệt biên bản'}
            </button>
            <button type="submit" value="tra-lai" disabled={dangXuLy}
              className="rounded-lg border border-slate-300 px-4 py-2 text-sm font-semibold disabled:opacity-60">
              Trả lại để chỉnh sửa
            </button>
          </div>
        </form>
      )}
      {coThePhatHanh && (
        <button type="button" disabled={dangXuLy} onClick={xuLyPhatHanh}
          className="rounded-lg bg-emerald-800 px-4 py-2 text-sm font-semibold text-white disabled:opacity-60">
          {dangXuLy ? 'Đang phát hành…' : laKetLuan ? 'Phát hành kết luận' : 'Phát hành biên bản'}
        </button>
      )}
    </KhongGianVanBan>
  )
}
