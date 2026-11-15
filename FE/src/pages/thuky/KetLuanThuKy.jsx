import { useEffect, useState } from 'react'
import {
  capNhatKetLuanNhap,
  layKetLuanTheoCuocHop,
  phatHanhKetLuan,
  trinhDuyetKetLuan,
  taoKetLuanNhap,
} from '../../api/ketLuanCuocHopApi.js'
import TaiLieuKetLuan from './TaiLieuKetLuan.jsx'

const LOP_TRUONG = 'mt-1 w-full rounded-lg border border-slate-300 px-3 py-2'

const TEN_TRANG_THAI = {
  NHAP: 'Bản nháp',
  BI_TRA_LAI: 'Cần chỉnh sửa',
  CHO_DUYET: 'Đang chờ phê duyệt',
  DA_DUYET: 'Đã duyệt · chờ thư ký cuộc họp phát hành',
  DA_PHAT_HANH: 'Đã phát hành',
}

export default function KetLuanThuKy({ cuocHopId, tieuDeCuocHop, token, onPhatHanh }) {
  const [ketLuan, setKetLuan] = useState(undefined)
  const [loiTai, setLoiTai] = useState('')
  const [loiLuu, setLoiLuu] = useState('')
  const [dangLuu, setDangLuu] = useState(false)
  const [loiTrinh, setLoiTrinh] = useState('')
  const [thongBao, setThongBao] = useState('')
  const coTheSua = ketLuan === null
    || ['NHAP', 'BI_TRA_LAI'].includes(ketLuan?.trangThai)

  useEffect(() => {
    const controller = new AbortController()

    layKetLuanTheoCuocHop(cuocHopId, token, controller.signal)
      .then((duLieu) => {
        if (!controller.signal.aborted) setKetLuan(duLieu)
      })
      .catch((loi) => {
        if (!controller.signal.aborted) setLoiTai(loi.message)
      })

    return () => controller.abort()
  }, [cuocHopId, token])

  async function xuLyLuu(event) {
    event.preventDefault()
    if (dangLuu) return

    const form = new FormData(event.currentTarget)
    const tieuDe = form.get('tieuDe').trim()
    const noiDung = form.get('noiDung').trim()
    const ghiChuTrinh = form.get('ghiChuTrinh')?.trim() || null
    const thaoTac = event.nativeEvent.submitter?.value

    setLoiLuu('')
    setLoiTrinh('')
    setThongBao('')

    if (!tieuDe) {
      setLoiLuu('Vui lòng nhập tiêu đề kết luận.')
      return
    }

    setDangLuu(true)

    try {
      const duLieu = { tieuDe, noiDung: noiDung || null }
      const ketLuanMoi = ketLuan?.ketLuanId
        ? await capNhatKetLuanNhap(cuocHopId, duLieu, token)
        : await taoKetLuanNhap(cuocHopId, duLieu, token)
      setKetLuan(ketLuanMoi)
      if (thaoTac === 'trinh') {
        const ketQuaTrinh = await trinhDuyetKetLuan(cuocHopId, ghiChuTrinh, token)
        setKetLuan((hienTai) => ({
          ...hienTai,
          trangThai: ketQuaTrinh.trangThai,
          yeuCauPheDuyetId: ketQuaTrinh.yeuCauPheDuyetId,
          capPheDuyet: 'CHU_TRI',
        }))
        setThongBao(ketQuaTrinh.message)
      } else {
        setThongBao('Đã lưu kết luận.')
      }
    } catch (loi) {
      setLoiLuu(loi.message)
    } finally {
      setDangLuu(false)
    }

  }

  async function xuLyPhatHanh() {
    if (dangLuu) return
    setDangLuu(true)
    setLoiLuu('')
    try {
      setKetLuan(await phatHanhKetLuan(cuocHopId, token))
      onPhatHanh?.()
    } catch (loi) {
      setLoiLuu(loi.message)
    } finally {
      setDangLuu(false)
    }
  }

  return (
    <section className="space-y-4 border-t border-slate-200 pt-5">
      <h3 className="font-semibold">Kết luận cuộc họp</h3>

      {loiTai ? (
        <p role="alert" className="text-sm text-red-800">{loiTai}</p>
      ) : ketLuan === undefined ? (
        <p role="status" className="text-sm text-slate-600">Đang tải kết luận…</p>
      ) : coTheSua ? (
        <form onSubmit={xuLyLuu} className="space-y-4">
          {ketLuan?.trangThai === 'BI_TRA_LAI' && ketLuan.ghiChuPhanHoi && (
            <p className="rounded-lg bg-amber-50 p-3 text-sm text-amber-950">
              Lý do trả lại: {ketLuan.ghiChuPhanHoi}
            </p>
          )}
          <label className="block text-sm font-medium">
            Tiêu đề kết luận
            <input
              name="tieuDe"
              key={`tieu-de-${ketLuan?.ketLuanId || 'moi'}`}
              defaultValue={ketLuan?.tieuDe
                ?? `Kết luận cuộc họp: ${tieuDeCuocHop || cuocHopId}`.slice(0, 255)}
              maxLength={255}
              required
              className={LOP_TRUONG}
            />
          </label>

          <label className="block text-sm font-medium">
            Nội dung kết luận
            <textarea
              key={`noi-dung-${ketLuan?.ketLuanId || 'moi'}`}
              name="noiDung"
              defaultValue={ketLuan?.noiDung ?? ''}
              rows={5}
              className={LOP_TRUONG}
            />
          </label>

          {loiLuu && <p role="alert" className="text-sm text-red-800">{loiLuu}</p>}
          {loiTrinh && <p role="alert" className="text-sm text-red-800">{loiTrinh}</p>}
          {thongBao && <p role="status" className="text-sm text-emerald-800">{thongBao}</p>}

          {ketLuan && (
            <label className="block text-sm font-medium">
              Ghi chú trình duyệt (không bắt buộc)
              <textarea name="ghiChuTrinh" rows={2} className={LOP_TRUONG} />
            </label>
          )}

          <button
            type="submit"
            value="luu"
            disabled={dangLuu}
            className="rounded-lg bg-emerald-800 px-4 py-2 text-sm font-semibold text-white disabled:opacity-60"
          >
            {dangLuu ? 'Đang lưu…' : ketLuan ? 'Lưu chỉnh sửa' : 'Lưu nháp kết luận'}
          </button>
          {ketLuan && (
            <button
              type="submit"
              value="trinh"
              disabled={dangLuu}
              className="ml-2 rounded-lg bg-blue-800 px-4 py-2 text-sm font-semibold text-white disabled:opacity-60"
            >
              {dangLuu ? 'Đang lưu và trình…' : 'Lưu và trình duyệt'}
            </button>
          )}
        </form>
      ) : (
        <div className="space-y-3 rounded-lg border border-slate-200 p-4">
          <h4 className="font-semibold">{ketLuan.tieuDe}</h4>
          <p className="text-sm text-slate-600">
            Kết luận #{ketLuan.ketLuanId} ·{' '}
            {TEN_TRANG_THAI[ketLuan.trangThai] ?? ketLuan.trangThai}
            {ketLuan.capPheDuyet && ketLuan.trangThai === 'CHO_DUYET'
              ? ` · Chờ ${ketLuan.capPheDuyet === 'CHU_TRI' ? 'chủ trì' : 'lãnh đạo'}${ketLuan.tenNguoiDuyet ? ` ${ketLuan.tenNguoiDuyet}` : ''}`
              : ''}
          </p>

          {ketLuan.ghiChuPhanHoi && (
            <p className="rounded-lg bg-amber-50 p-3 text-sm text-amber-950">
              Lý do trả lại: {ketLuan.ghiChuPhanHoi}
            </p>
          )}
          <p className="whitespace-pre-wrap text-sm">
            {ketLuan.noiDung || 'Chưa có nội dung văn bản.'}
          </p>

          {ketLuan.hanPhatHanh && (
            <p className="text-sm text-slate-600">
              Hạn phát hành {ketLuan.hanPhatHanh}
              {ketLuan.quaHanPhatHanh && ' · Quá hạn'}
            </p>
          )}
          {thongBao && <p role="status" className="text-sm text-emerald-800">{thongBao}</p>}
          {ketLuan.trangThai === 'DA_DUYET' && (
            <button type="button" disabled={dangLuu} onClick={xuLyPhatHanh}
              className="rounded-lg bg-emerald-800 px-4 py-2 text-sm font-semibold text-white disabled:opacity-60">Phát hành kết luận</button>
          )}
          {loiLuu && <p role="alert" className="text-sm text-red-800">{loiLuu}</p>}
          <TaiLieuKetLuan
            cuocHopId={cuocHopId}
            token={token}
            choPhepTaiLen={['NHAP', 'BI_TRA_LAI'].includes(ketLuan.trangThai)}
            />
        </div>
      )}
    </section>
  )
}



