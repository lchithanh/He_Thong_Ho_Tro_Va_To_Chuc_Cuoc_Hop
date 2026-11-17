import { useEffect, useState } from 'react'
import { LichCuaHoSo } from '../thuky/DanhSachDeXuatThuKy.jsx'
import {
  layKetLuanChoToiPhatHanh,
  phatHanhKetLuan,
  xuLyDuyetKetLuan,
} from '../../api/ketLuanCuocHopApi.js'
import TaiLieuKetLuan from '../thuky/TaiLieuKetLuan.jsx'
import BanXemTruocVanBan from '../thuky/BanXemTruocVanBan.jsx'
import KhongGianVanBan from '../thuky/KhongGianVanBan.jsx'

export default function KetLuanChoPhatHanh({ token, cheDo }) {
  const [danhSach, setDanhSach] = useState(null)
  const [dangMo, setDangMo] = useState({})
  const [loi, setLoi] = useState('')
  const [thongBao, setThongBao] = useState('')
  const [dangPhatHanhId, setDangPhatHanhId] = useState(null)
  const [lanTai, setLanTai] = useState(0)

  useEffect(() => {
    const controller = new AbortController()

    layKetLuanChoToiPhatHanh(token, controller.signal)
      .then((duLieu) => {
        if (!controller.signal.aborted) setDanhSach(duLieu)
      })
      .catch((loiTai) => {
        if (!controller.signal.aborted) setLoi(loiTai.message)
      })

    return () => controller.abort()
  }, [token, lanTai])

  async function xuLyPhatHanh(ketLuan) {
    if (dangPhatHanhId !== null) return

    setDangPhatHanhId(ketLuan.ketLuanId)
    setLoi('')
    setThongBao('')

    try {
      const ketQua = await phatHanhKetLuan(ketLuan.cuocHopId, token)
      setDanhSach((hienTai) =>
        hienTai.filter((muc) => muc.ketLuanId !== ketLuan.ketLuanId),
      )
      setThongBao(ketQua.trangThai === 'DA_DUYET'
        ? 'Đã duyệt kết luận. Thư ký cuộc họp có thể phát hành.'
        : `Đã phát hành kết luận cuộc họp #${ketLuan.cuocHopId}.`)
    } catch (loiPhatHanh) {
      setLoi(loiPhatHanh.message)
    } finally {
      setDangPhatHanhId(null)
    }
  }

  async function xuLyDuyet(event, ketLuan) {
    event.preventDefault()
    if (dangPhatHanhId !== null) return

    const form = new FormData(event.currentTarget)
    const thaoTac = event.nativeEvent.submitter?.value
    const yKien = form.get('yKien').trim()
    if (thaoTac === 'tra-lai' && !yKien) {
      setLoi('Vui lòng nhập lý do trả lại kết luận.')
      return
    }

    setDangPhatHanhId(ketLuan.ketLuanId)
    setLoi('')
    setThongBao('')
    try {
      const ketQua = await xuLyDuyetKetLuan(ketLuan, thaoTac, yKien || null, token)
      setThongBao(ketQua.message)
      if (thaoTac === 'tra-lai') {
        setDanhSach((hienTai) =>
          hienTai.filter((muc) => muc.ketLuanId !== ketLuan.ketLuanId),
        )
      } else {
        setLanTai((hienTai) => hienTai + 1)
      }
    } catch (loiXuLy) {
      setLoi(loiXuLy.message)
    } finally {
      setDangPhatHanhId(null)
    }
  }

  const danhSachHienThi = danhSach?.filter((ketLuan) => cheDo === 'duyet'
    ? ketLuan.trangThai === 'CHO_DUYET'
    : ketLuan.trangThai === 'DA_DUYET')

  return (
    <section className="space-y-4 rounded-xl border border-slate-200 bg-white p-5">
      <h2 className="text-lg font-bold">{cheDo === 'duyet' ? 'Kết luận chờ duyệt' : 'Kết luận chờ phát hành'}</h2>

      {loi && <p role="alert" className="text-sm text-red-800">{loi}</p>}
      {thongBao && <p role="status" className="text-sm text-emerald-800">{thongBao}</p>}
      {!loi && danhSach === null && <p role="status">Đang tải kết luận…</p>}
      {danhSachHienThi?.length === 0 && (
        <p className="text-sm text-slate-600">{cheDo === 'duyet' ? 'Chưa có kết luận chờ bạn duyệt.' : 'Chưa có kết luận chờ bạn phát hành.'}</p>
      )}

      {danhSachHienThi?.map((ketLuan) => (
        <details key={ketLuan.ketLuanId}
          onToggle={(event) => {
            const mo = event.currentTarget.open
            setDangMo((hienTai) => ({ ...hienTai, [ketLuan.ketLuanId]: mo }))
          }}
          className="rounded-lg border border-slate-200 p-4">
          <summary className="cursor-pointer font-semibold">
            {ketLuan.tieuDeCuocHop || ketLuan.tieuDe}
          </summary>

          <KhongGianVanBan banXem={dangMo[ketLuan.ketLuanId] && (
              <BanXemTruocVanBan tenLoai="Kết luận cuộc họp" tieuDe={ketLuan.tieuDe}
                noiDung={ketLuan.noiDung} trangThai={ketLuan.trangThai === 'CHO_DUYET'
                  ? 'Chờ chủ trì duyệt' : 'Đã duyệt · chờ chủ trì phát hành'} />
            )}>
          <div className="mt-3 space-y-3">
            <p className="text-xs text-slate-500">Cuộc họp #{ketLuan.cuocHopId}
              {ketLuan.hanPhatHanh ? ` · Hạn phát hành ${ketLuan.hanPhatHanh.split('-').reverse().join('/')}` : ''}
            </p>
            {dangMo[ketLuan.ketLuanId] && <LichCuaHoSo cuocHopId={ketLuan.cuocHopId} token={token} />}
            {dangMo[ketLuan.ketLuanId] && <TaiLieuKetLuan
              cuocHopId={ketLuan.cuocHopId}
              token={token}
              choPhepTaiLen={false}
            />}

            {ketLuan.trangThai === 'CHO_DUYET' && (
              <form onSubmit={(event) => xuLyDuyet(event, ketLuan)} className="space-y-3">
                <label className="block text-sm font-medium">
                  Ý kiến xử lý
                  <textarea
                    name="yKien"
                    rows={2}
                    disabled={dangPhatHanhId !== null}
                    className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
                  />
                </label>
                <div className="flex flex-wrap gap-2">
                  <button
                    type="submit"
                    value="phe-duyet"
                    disabled={dangPhatHanhId !== null}
                    className="rounded-lg bg-emerald-800 px-4 py-2 text-sm font-semibold text-white disabled:opacity-60"
                  >
                    {dangPhatHanhId === ketLuan.ketLuanId ? 'Đang xử lý…' : 'Phê duyệt'}
                  </button>
                  <button
                    type="submit"
                    value="tra-lai"
                    disabled={dangPhatHanhId !== null}
                    className="rounded-lg border border-slate-300 px-4 py-2 text-sm font-semibold disabled:opacity-60"
                  >
                    Trả lại để chỉnh sửa
                  </button>
                </div>
              </form>
            )}
            {ketLuan.trangThai === 'DA_DUYET' && (
              <button
                type="button"
                disabled={dangPhatHanhId !== null}
                onClick={() => xuLyPhatHanh(ketLuan)}
                className="rounded-lg bg-emerald-800 px-4 py-2 text-sm font-semibold text-white disabled:opacity-60"
              >
                {dangPhatHanhId === ketLuan.ketLuanId
                  ? 'Đang phát hành…'
                  : 'Phát hành kết luận'}
              </button>
            )}
          </div>
          </KhongGianVanBan>
        </details>
      ))}
    </section>
  )
}



