import { useEffect, useState } from 'react'
import {
  capNhatBienBan,
  layBienBanTheoCuocHop,
  layLichSuPhienBan,
  phatHanhBienBan,
  taoBienBanCuocHop,
  trinhDuyetBienBan,
} from '../../api/bienBanApi.js'

const LOP_TRUONG = 'mt-1 w-full rounded-lg border border-slate-300 px-3 py-2'

const TEN_TRANG_THAI = {
  NHAP: 'Bản nháp',
  CHO_DUYET: 'Chờ duyệt',
  DA_PHE_DUYET: 'Đã phê duyệt',
  DA_PHAT_HANH: 'Đã phát hành',
  BI_TRA_LAI: 'Bị trả lại',
}

export default function BienBanThuKy({ cuocHopId, tieuDeCuocHop, token, onPhatHanh }) {
  const [bienBan, setBienBan] = useState(undefined)
  const [loiTai, setLoiTai] = useState('')
  const [loiLuu, setLoiLuu] = useState('')
  const [dangLuu, setDangLuu] = useState(false)
  const [dangSua, setDangSua] = useState(false)
  const [phienBan, setPhienBan] = useState([])
  const [loiPhienBan, setLoiPhienBan] = useState('')
  const [lanLuu, setLanLuu] = useState(0)

  const bienBanId = bienBan?.bienBanId
  const coTheSua = ['NHAP', 'BI_TRA_LAI'].includes(bienBan?.trangThai)

  const [dangTrinh, setDangTrinh] = useState(false)
const [loiTrinh, setLoiTrinh] = useState('')
const [yeuCauPheDuyetId, setYeuCauPheDuyetId] = useState(null)

  useEffect(() => {
    const controller = new AbortController()

    layBienBanTheoCuocHop(cuocHopId, token, controller.signal)
      .then((duLieu) => {
        if (!controller.signal.aborted) setBienBan(duLieu)
      })
      .catch((loi) => {
        if (!controller.signal.aborted) setLoiTai(loi.message)
      })

    return () => controller.abort()
  }, [cuocHopId, token])

  useEffect(() => {
    if (!bienBanId) return

    const controller = new AbortController()

    layLichSuPhienBan(bienBanId, token, controller.signal)
      .then((duLieu) => {
        if (controller.signal.aborted) return
        setPhienBan(duLieu)
        setLoiPhienBan('')
      })
      .catch((loi) => {
        if (!controller.signal.aborted) setLoiPhienBan(loi.message)
      })

    return () => controller.abort()
  }, [bienBanId, token, lanLuu])

  async function xuLyLuu(event) {
    event.preventDefault()
    if (dangLuu) return

    const form = new FormData(event.currentTarget)
    const tieuDe = form.get('tieuDe').trim()
    const noiDungGoc = form.get('noiDungGoc').trim()
    const noiDungChinhThuc = form.get('noiDungChinhThuc').trim()

    setLoiLuu('')

    if (!tieuDe || !noiDungChinhThuc) {
      setLoiLuu('Vui lòng nhập tiêu đề và nội dung chính thức.')
      return
    }

    const duLieu = {
      tieuDe,
      noiDungGoc: noiDungGoc || null,
      noiDungChinhThuc,
      ghiChuThayDoi: form.get('ghiChuThayDoi')?.trim() || null,
    }

    setDangLuu(true)

    try {
      const bienBanMoi = bienBanId
        ? await capNhatBienBan(bienBanId, duLieu, token)
        : await taoBienBanCuocHop(cuocHopId, duLieu, token)

      setBienBan(bienBanMoi)
      setDangSua(false)
      setLanLuu((hienTai) => hienTai + 1)
    } catch (loi) {
      setLoiLuu(loi.message)
    } finally {
      setDangLuu(false)
    }
  }

  async function xuLyTrinh(event) {
  event.preventDefault()
  if (dangTrinh || !coTheSua) return

  const form = new FormData(event.currentTarget)
  const ghiChuTrinh = form.get('ghiChuTrinh').trim() || null

  setDangTrinh(true)
  setLoiTrinh('')

  try {
    const ketQua = await trinhDuyetBienBan(bienBanId, ghiChuTrinh, token)

    setBienBan((hienTai) => ({
      ...hienTai,
      trangThai: ketQua.trangThai,
    }))
    setYeuCauPheDuyetId(ketQua.yeuCauPheDuyetId)
  } catch (loi) {
    setLoiTrinh(loi.message)
  } finally {
    setDangTrinh(false)
  }
}

  async function xuLyPhatHanh() {
    if (dangLuu) return
    setDangLuu(true)
    setLoiLuu('')
    try {
      setBienBan(await phatHanhBienBan(cuocHopId, token))
      onPhatHanh?.()
    } catch (loi) {
      setLoiLuu(loi.message)
    } finally {
      setDangLuu(false)
    }
  }

  return (
    <section className="space-y-4 border-t border-slate-200 pt-5">
      <h3 className="font-semibold">Biên bản cuộc họp</h3>

      {loiTai ? (
        <p role="alert" className="text-sm text-red-800">{loiTai}</p>
      ) : bienBan === undefined ? (
        <p role="status" className="text-sm text-slate-600">Đang tải biên bản…</p>
      ) : bienBan === null || dangSua ? (
        <form onSubmit={xuLyLuu} className="space-y-4">
          {bienBan?.trangThai === 'BI_TRA_LAI' && bienBan.yKienPhanHoi && (
            <p className="rounded-lg bg-amber-50 p-3 text-sm text-amber-950">
              Lý do trả lại: {bienBan.yKienPhanHoi}
            </p>
          )}
          <label className="block text-sm font-medium">
            Tiêu đề biên bản
            <input
              name="tieuDe"
              defaultValue={
                bienBan?.tieuDe ??
                `Biên bản cuộc họp: ${tieuDeCuocHop || cuocHopId}`.slice(0, 255)
              }
              maxLength={255}
              required
              className={LOP_TRUONG}
            />
          </label>

          <label className="block text-sm font-medium">
            Ghi chú gốc
            <textarea
              name="noiDungGoc"
              defaultValue={bienBan?.noiDungGoc ?? ''}
              rows={3}
              className={LOP_TRUONG}
            />
          </label>

          <label className="block text-sm font-medium">
            Nội dung chính thức
            <textarea
              name="noiDungChinhThuc"
              defaultValue={bienBan?.noiDungChinhThuc ?? ''}
              rows={6}
              required
              className={LOP_TRUONG}
            />
          </label>

          {dangSua && (
            <label className="block text-sm font-medium">
              Ghi chú thay đổi
              <input name="ghiChuThayDoi" className={LOP_TRUONG} />
            </label>
          )}

          {loiLuu && <p role="alert" className="text-sm text-red-800">{loiLuu}</p>}

          <button
            type="submit"
            disabled={dangLuu}
            className="rounded-lg bg-emerald-800 px-4 py-2 text-sm font-semibold text-white disabled:opacity-60"
          >
            {dangLuu ? 'Đang lưu…' : dangSua ? 'Lưu chỉnh sửa' : 'Lưu nháp biên bản'}
          </button>
        </form>
      ) : (
        <div className="space-y-3 rounded-lg border border-slate-200 p-4">
          <div>
            <h4 className="font-semibold">{bienBan.tieuDe}</h4>
            <p className="text-sm text-slate-600">
              Biên bản #{bienBan.bienBanId} ·{' '}
              {TEN_TRANG_THAI[bienBan.trangThai] ?? bienBan.trangThai}
              {bienBan.trangThai === 'CHO_DUYET' && bienBan.tenNguoiDuyet
                ? ` · Đang chờ ${bienBan.tenNguoiDuyet}`
                : ''}
            </p>
          </div>

          <p className="whitespace-pre-wrap text-sm">{bienBan.noiDungChinhThuc}</p>
          {bienBan.trangThai === 'BI_TRA_LAI' && bienBan.yKienPhanHoi && (
            <p className="rounded-lg bg-amber-50 p-3 text-sm text-amber-950">
              Lý do trả lại: {bienBan.yKienPhanHoi}
            </p>
          )}

          
                    {coTheSua && !dangSua && (
            <button
              type="button"
              onClick={() => setDangSua(true)}
              className="rounded-lg border border-slate-300 px-4 py-2 text-sm font-semibold"
            >
              Sửa biên bản
            </button>
            )}
                    {coTheSua && (
            <form onSubmit={xuLyTrinh} className="space-y-3 border-t border-slate-200 pt-4">
                <label className="block text-sm font-medium">
                Ghi chú trình duyệt
                <textarea
                    name="ghiChuTrinh"
                    rows={2}
                    disabled={dangTrinh}
                    className={LOP_TRUONG}
                />
                </label>

                {loiTrinh && <p role="alert" className="text-sm text-red-800">{loiTrinh}</p>}

                <button
                type="submit"
                disabled={dangTrinh}
                className="rounded-lg bg-emerald-800 px-4 py-2 text-sm font-semibold text-white disabled:opacity-60"
                >
                {dangTrinh ? 'Đang trình…' : 'Trình duyệt biên bản'}
                </button>
            </form>
            )}

            {yeuCauPheDuyetId !== null && (
            <p role="status" className="rounded-lg bg-emerald-50 p-3 text-sm text-emerald-800">
                Đã trình duyệt biên bản. Mã yêu cầu #{yeuCauPheDuyetId}.
            </p>
            )}
        </div>
      )}

      {bienBan?.trangThai === 'DA_PHE_DUYET' && (
        <button type="button" disabled={dangLuu} onClick={xuLyPhatHanh}
          className="rounded-lg bg-emerald-800 px-4 py-2 text-sm font-semibold text-white disabled:opacity-60">
          Phát hành biên bản
        </button>
      )}
      {bienBan?.trangThai === 'DA_PHE_DUYET' && loiLuu && <p role="alert" className="text-sm text-red-800">{loiLuu}</p>}

      {bienBanId && (
        <div className="space-y-2 border-t border-slate-200 pt-4">
          <h4 className="font-semibold">Lịch sử phiên bản</h4>

          {loiPhienBan && (
            <p role="alert" className="text-sm text-red-800">{loiPhienBan}</p>
          )}

          {phienBan.map((muc) => (
            <details
              key={muc.phienBanId}
              className="rounded-lg border border-slate-200 p-3 text-sm"
            >
              <summary className="cursor-pointer font-medium">
                Phiên bản {muc.soPhienBan}
                {muc.ghiChuThayDoi ? ` · ${muc.ghiChuThayDoi}` : ''}
              </summary>
              <p className="mt-2 whitespace-pre-wrap">{muc.noiDung}</p>
            </details>
          ))}
        </div>
      )}
    </section>
  )
}



