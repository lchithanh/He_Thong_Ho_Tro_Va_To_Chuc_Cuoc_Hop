import { useEffect, useState } from 'react'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import {
  dongBieuQuyet,
  layDanhSachBieuQuyetCuaChuTri,
  layKetQuaBieuQuyet,
  moBieuQuyet,
} from '../../api/bieuQuyetApi.js'
const TEN_TRANG_THAI = {
  NHAP: 'Bản nháp',
  DANG_MO: 'Đang mở',
  DA_DONG: 'Đã đóng',
}

export default function DanhSachBieuQuyetChuTri({
  cuocHopId, token, dangDienRa, lanTao,
}) {
  const phienDongBo = useDongBoDuLieu()
  const [danhSach, setDanhSach] = useState(null)
  const [loi, setLoi] = useState('')
  const [idDangMo, setIdDangMo] = useState(null)
  const [idDangXuLyKetQua, setIdDangXuLyKetQua] = useState(null)
const [ketQuaDangXem, setKetQuaDangXem] = useState(null)
  const dangXuLy = idDangMo !== null || idDangXuLyKetQua !== null

  useEffect(() => {
    const controller = new AbortController()

    layDanhSachBieuQuyetCuaChuTri(cuocHopId, token, controller.signal)
      .then((duLieu) => {
        if (controller.signal.aborted) return
        setDanhSach(duLieu)
        setLoi('')
      })
      .catch((loiTai) => {
        if (!controller.signal.aborted) setLoi(loiTai.message)
      })

    return () => controller.abort()
  }, [cuocHopId, token, lanTao, phienDongBo])

  async function xuLyMo(bieuQuyetId) {
    if (idDangMo !== null) return

    setIdDangMo(bieuQuyetId)
    setLoi('')

    try {
      await moBieuQuyet(cuocHopId, bieuQuyetId, token)
      setDanhSach((hienTai) =>
        hienTai.map((muc) =>
          muc.bieuQuyetId === bieuQuyetId
            ? { ...muc, trangThai: 'DANG_MO' }
            : muc,
        ),
      )
    } catch (loiMo) {
      setLoi(loiMo.message)
    } finally {
      setIdDangMo(null)
    }
  }

  async function xuLyKetQua(bieuQuyetId, canDong) {
  if (dangXuLy) return

  setIdDangXuLyKetQua(bieuQuyetId)
  setKetQuaDangXem(null)
  setLoi('')

  try {
    const ketQua = canDong
      ? await dongBieuQuyet(cuocHopId, bieuQuyetId, token)
      : await layKetQuaBieuQuyet(cuocHopId, bieuQuyetId, token)

    if (canDong) {
      setDanhSach((hienTai) =>
        hienTai.map((muc) =>
          muc.bieuQuyetId === bieuQuyetId
            ? { ...muc, trangThai: 'DA_DONG' }
            : muc,
        ),
      )
    }

    setKetQuaDangXem({ bieuQuyetId, duLieu: ketQua })
  } catch (loiXuLy) {
    setLoi(loiXuLy.message)
  } finally {
    setIdDangXuLyKetQua(null)
  }
}
  return (
    <div className="space-y-3 border-t border-slate-200 pt-4">
      <h3 className="font-semibold">Biểu quyết của cuộc họp</h3>

      {loi && <p role="alert" className="text-sm text-red-800">{loi}</p>}
      {!loi && danhSach === null && <p role="status">Đang tải biểu quyết…</p>}
      {danhSach?.length === 0 && <p>Cuộc họp chưa có biểu quyết.</p>}

      {danhSach?.map((muc) => (
        <div key={muc.bieuQuyetId} className="space-y-2 rounded-lg border border-slate-200 p-3">
          <p className="font-medium">{muc.tieuDe}</p>
          <p className="text-sm text-slate-600">
            #{muc.bieuQuyetId} · {TEN_TRANG_THAI[muc.trangThai] ?? muc.trangThai}
          </p>
          <p className="text-sm text-slate-600">
            {new Date(muc.thoiGianBatDau).toLocaleString('vi-VN')} –{' '}
            {new Date(muc.thoiGianKetThuc).toLocaleString('vi-VN')}
          </p>

          {dangDienRa && muc.trangThai === 'NHAP' && (
            <button
              type="button"
              disabled={idDangMo !== null}
              onClick={() => xuLyMo(muc.bieuQuyetId)}
              className="rounded-lg bg-emerald-800 px-3 py-2 text-sm font-semibold text-white disabled:opacity-60"
            >
              {idDangMo === muc.bieuQuyetId ? 'Đang mở…' : 'Mở biểu quyết'}

            </button>
          )}
          {['DANG_MO', 'DA_DONG'].includes(muc.trangThai) && (
            <button
                type="button"
                disabled={dangXuLy}
                onClick={() => xuLyKetQua(muc.bieuQuyetId, muc.trangThai === 'DANG_MO')}
                className="rounded-lg border border-slate-300 px-3 py-2 text-sm font-semibold disabled:opacity-60"
            >
                {idDangXuLyKetQua === muc.bieuQuyetId
                ? 'Đang xử lý…'
                : muc.trangThai === 'DANG_MO'
                    ? 'Đóng biểu quyết'
                    : 'Xem kết quả'}
            </button>
            )}

            {ketQuaDangXem?.bieuQuyetId === muc.bieuQuyetId && (
            <div className="space-y-2 rounded-lg bg-slate-50 p-3 text-sm">
                <p className="font-semibold">Kết quả biểu quyết</p>
                <p>Tổng số phiếu: {ketQuaDangXem.duLieu.tongSoPhieu}</p>
                {ketQuaDangXem.duLieu.tongSoPhieu === 0 && (
                  <p>Chưa có phiếu; chưa thể xác định kết quả.</p>
                )}
                {ketQuaDangXem.duLieu.hoaPhieu && (
                  <p>Hòa phiếu; chưa có phương án thắng.</p>
                )}
                {ketQuaDangXem.duLieu.luaChonThangId != null && (
                  <p>
                    Phương án nhiều phiếu nhất:{' '}
                    {ketQuaDangXem.duLieu.luaChon.find(
                      (luaChon) => luaChon.luaChonId === ketQuaDangXem.duLieu.luaChonThangId,
                    )?.noiDung}
                  </p>
                )}

               <ul className="space-y-1">
                {ketQuaDangXem.duLieu.luaChon?.map((luaChon) => (
                    <li key={luaChon.luaChonId}>
                    {luaChon.noiDung}: {luaChon.soPhieu} phiếu
                    </li>
                ))}
                </ul>
            </div>
            )}
        </div>
      ))}
    </div>
  )
}