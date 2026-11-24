import Vote from 'lucide-react/dist/esm/icons/vote.mjs'
import { LocHoSo, NhanHoSo, TieuDeHoSo } from '../../components/GiaoDienPhongHop.jsx'
import { khopTuKhoa, LOP_CHU_PHU, LOP_THE, LOP_NOI, LOP_NUT_CHINH, LOP_NUT_PHU, LOP_NUT_XOA } from '../../components/giaoDienPhongHop.js'
import BangKetQuaBieuQuyet from '../../components/BangKetQuaBieuQuyet.jsx'
import KhungGioBieuQuyet from '../../components/KhungGioBieuQuyet.jsx'
import { useEffect, useState } from 'react'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import {
  dongBieuQuyet,
  layDanhSachBieuQuyetCuaChuTri,
  layKetQuaBieuQuyet,
  moBieuQuyet,
  xoaBieuQuyet,
} from '../../api/bieuQuyetApi.js'
const TEN_PHUONG_THUC = {
  TAN_THANH: 'Đồng ý / Không đồng ý / Phiếu trắng',
  CHON_MOT: 'Chọn một phương án',
  CHON_NHIEU: 'Chọn nhiều (phiên cũ)',
}
const TEN_TRANG_THAI = {
  NHAP: 'Bản nháp',
  DANG_MO: 'Đang mở',
  DA_DONG: 'Đã đóng',
}

const MAU_TRANG_THAI = { DANG_MO: 'luc', DA_DONG: 'xanh' }
const BO_LOC = [['', 'Tất cả'], ...Object.entries(TEN_TRANG_THAI)]

export default function DanhSachBieuQuyetChuTri({
  cuocHopId, token, dangDienRa, lanTao, nghiQuyetId, onSua,
}) {
  const phienDongBo = useDongBoDuLieu()
  const [danhSach, setDanhSach] = useState(null)
  const [loi, setLoi] = useState('')
  const [idDangMo, setIdDangMo] = useState(null)
  const [idDangXuLyKetQua, setIdDangXuLyKetQua] = useState(null)
  const [ketQuaDangXem, setKetQuaDangXem] = useState(null)
  const [dangXoa, setDangXoa] = useState(false)
  const dangXuLy = idDangMo !== null || idDangXuLyKetQua !== null || dangXoa
  const [tuKhoa, setTuKhoa] = useState('')
  const [boLoc, setBoLoc] = useState('')
  const danhSachLoc = nghiQuyetId == null ? danhSach : danhSach?.filter((muc) => muc.nghiQuyetId === nghiQuyetId)

  const hienThi = danhSachLoc?.filter((muc) => (!boLoc || muc.trangThai === boLoc) && khopTuKhoa(muc.tieuDe, tuKhoa))

  async function xuLyXoa(muc) {
    if (dangXuLy || !window.confirm(`Xóa bản nháp “${muc.tieuDe}”?`)) return
    setDangXoa(true)
    setLoi('')
    try {
      await xoaBieuQuyet(cuocHopId, muc.bieuQuyetId, token)
      setDanhSach((hienTai) => hienTai?.filter((phien) => phien.bieuQuyetId !== muc.bieuQuyetId))
    } catch (error) {
      setLoi(error.message)
    } finally {
      setDangXoa(false)
    }
  }

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
    if (dangXuLy) return

    setIdDangMo(bieuQuyetId)
    setLoi('')

    try {
      await moBieuQuyet(cuocHopId, bieuQuyetId, token)
      setDanhSach(await layDanhSachBieuQuyetCuaChuTri(cuocHopId, token))
      if (boLoc === 'NHAP') setBoLoc('DANG_MO')
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

      if (canDong && boLoc === 'DANG_MO') setBoLoc('DA_DONG')
      setKetQuaDangXem({ bieuQuyetId, duLieu: ketQua })
    } catch (loiXuLy) {
      setLoi(loiXuLy.message)
    } finally {
      setIdDangXuLyKetQua(null)
    }
  }
  return (
    <div className="space-y-3">
      <div className={LOP_THE + ' space-y-4'}>
        <TieuDeHoSo Icon={Vote}>Biểu quyết của cuộc họp</TieuDeHoSo>
        {danhSachLoc?.length > 0 && <LocHoSo tuKhoa={tuKhoa} onTuKhoa={setTuKhoa} nhanTim="Tìm biểu quyết"
          boLoc={boLoc} onBoLoc={setBoLoc} cacMuc={BO_LOC} />}
        {loi && <p role="alert" className="text-sm text-red-800 phong-toi:text-phong-do">{loi}</p>}
        {!loi && danhSach === null && <p role="status" className={LOP_CHU_PHU}>Đang tải biểu quyết…</p>}
        {hienThi?.length === 0 && <p role="status" className={LOP_CHU_PHU}>
          {danhSachLoc.length ? 'Không tìm thấy biểu quyết phù hợp.' : 'Cuộc họp chưa có biểu quyết.'}
        </p>}
      </div>

      {hienThi?.map((muc) => (
        <article key={muc.bieuQuyetId} className={LOP_THE + ' space-y-3'}>
          <TieuDeHoSo Icon={Vote} cap="h4" phu={TEN_PHUONG_THUC[muc.phuongThuc] ?? TEN_PHUONG_THUC.TAN_THANH}
            thaoTac={<NhanHoSo mau={MAU_TRANG_THAI[muc.trangThai]}>{TEN_TRANG_THAI[muc.trangThai] ?? muc.trangThai}</NhanHoSo>}>
            {muc.tieuDe}
          </TieuDeHoSo>
          <KhungGioBieuQuyet muc={muc} />

          <div className="flex flex-wrap gap-2">
            {dangDienRa && muc.trangThai === 'NHAP' && (
              <button type="button" disabled={dangXuLy} onClick={() => xuLyMo(muc.bieuQuyetId)} className={LOP_NUT_CHINH}>
                {idDangMo === muc.bieuQuyetId ? 'Đang mở…' : 'Mở biểu quyết'}
              </button>
            )}
            {onSua && muc.trangThai === 'NHAP' && (
              <>
                <button type="button" disabled={dangXuLy} onClick={() => onSua(muc.bieuQuyetId)} className={LOP_NUT_PHU}>Cập nhật</button>
                <button type="button" disabled={dangXuLy} onClick={() => xuLyXoa(muc)} className={LOP_NUT_XOA}>Xóa bản nháp</button>
              </>
            )}
            {['DANG_MO', 'DA_DONG'].includes(muc.trangThai) && (
              <button type="button" disabled={dangXuLy} onClick={() => xuLyKetQua(muc.bieuQuyetId, muc.trangThai === 'DANG_MO')}
                className={muc.trangThai === 'DANG_MO' ? LOP_NUT_XOA : LOP_NUT_PHU}>
                {idDangXuLyKetQua === muc.bieuQuyetId ? 'Đang xử lý…' : muc.trangThai === 'DANG_MO' ? 'Đóng biểu quyết' : 'Xem kết quả'}
              </button>
            )}
          </div>

          {ketQuaDangXem?.bieuQuyetId === muc.bieuQuyetId && (
            <div className={LOP_NOI + ' space-y-3 text-sm'}>
              <p className="font-semibold">Kết quả biểu quyết</p>
              <BangKetQuaBieuQuyet ketQua={ketQuaDangXem.duLieu} hienLyDo />
            </div>
          )}
        </article>
      ))}
    </div>
  )
}
