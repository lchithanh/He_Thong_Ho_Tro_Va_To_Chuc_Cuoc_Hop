import { useEffect, useState } from 'react'
import FileSignature from 'lucide-react/dist/esm/icons/file-signature.mjs'
import { HopThoaiHoSo, LocHoSo, NhanHoSo, NutTaoHoSo, TieuDeHoSo } from '../../components/GiaoDienPhongHop.jsx'
import { khopTuKhoa, LOP_CHU_PHU, LOP_NUT_CHINH, LOP_NUT_PHU, LOP_NUT_XOA, LOP_THE, LOP_TRUONG } from '../../components/giaoDienPhongHop.js'
import TaoBieuQuyet from '../pheduyet/TaoBieuQuyet.jsx'
import BieuQuyetThamDu from './BieuQuyetThamDu.jsx'
import TepHoSo from '../../components/TepHoSo.jsx'
import { goiNghiQuyet } from '../../api/nghiepVuCuocHopApi.js'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'

const TRANG_THAI = {
  NHAP: 'Bản nháp', DANG_BIEU_QUYET: 'Đã lập phiên biểu quyết',
  THONG_QUA: 'Thông qua', KHONG_THONG_QUA: 'Không thông qua',
  CHUA_DAT_DA_SO: 'Chưa đạt đa số', YEU_CAU_CHINH_SUA: 'Yêu cầu chỉnh sửa',
}
const MAU_TRANG_THAI = { DANG_BIEU_QUYET: 'xanh', THONG_QUA: 'luc', KHONG_THONG_QUA: 'do', CHUA_DAT_DA_SO: 'vang', YEU_CAU_CHINH_SUA: 'vang' }
const BO_LOC = [['', 'Tất cả'], ['NHAP', 'Dự thảo'], ['DANG_BIEU_QUYET', 'Biểu quyết'], ['THONG_QUA', 'Đã thông qua'], ['can-xu-ly', 'Cần xử lý']]
const TRUONG = LOP_TRUONG + ' mt-1'
const NUT = LOP_NUT_PHU

function CayNghiQuyet({ danhSach, chaId = null, children }) {
  return (
    <ul className={chaId == null ? 'space-y-3' : 'mt-2 space-y-2 border-l border-slate-200 pl-2 sm:pl-3 phong-toi:border-phong-vien'}>
      {danhSach.filter((muc) => muc.nghiQuyetChaId === chaId || (chaId === null
        && !danhSach.some((cha) => cha.nghiQuyetId === muc.nghiQuyetChaId))).map((muc) => (
        <li key={muc.nghiQuyetId} className={LOP_THE + ' space-y-3'}>
          <TieuDeHoSo Icon={FileSignature} cap="h4" phu={muc.cap > 1 ? `Nghị quyết cấp ${muc.cap}` : null}
            thaoTac={<NhanHoSo mau={MAU_TRANG_THAI[muc.trangThai]}>
              {muc.trangThai === 'NHAP' && muc.ngayCongBo ? 'Đã công bố' : TRANG_THAI[muc.trangThai]}
            </NhanHoSo>}>
            {muc.tieuDe}
          </TieuDeHoSo>
          {muc.noiDung && <p className="whitespace-pre-wrap wrap-anywhere">{muc.noiDung}</p>}
          {muc.lyDo && <p className="text-amber-950 phong-toi:text-phong-vang">Lý do chỉnh sửa · {muc.lyDo}</p>}
          {children(muc)}
          {danhSach.some((con) => con.nghiQuyetChaId === muc.nghiQuyetId) &&
            <CayNghiQuyet danhSach={danhSach} chaId={muc.nghiQuyetId}>{children}</CayNghiQuyet>}
        </li>
      ))}
    </ul>
  )
}

export default function NghiQuyetCuocHop({ cuocHopId, token, duocQuanLy = false, hienTieuDe = true, chuTri }) {
  const phien = useDongBoDuLieu()
  const [danhSach, setDanhSach] = useState(null)
  const [lanTai, setLanTai] = useState(0)
  const [mucSua, setMucSua] = useState(null)
  const [nghiQuyetDangBieuQuyet, setNghiQuyetDangBieuQuyet] = useState(null)
  const [dangGui, setDangGui] = useState(false)
  const [loi, setLoi] = useState('')
  const [tuKhoa, setTuKhoa] = useState('')
  const [boLoc, setBoLoc] = useState('')

  const mucKhop = danhSach?.filter((muc) => khopTuKhoa(muc.tieuDe, tuKhoa) && (!boLoc
    || (boLoc === 'can-xu-ly' ? ['KHONG_THONG_QUA', 'CHUA_DAT_DA_SO', 'YEU_CAU_CHINH_SUA'].includes(muc.trangThai) : muc.trangThai === boLoc)))
  const idHienThi = new Set(mucKhop?.map((muc) => muc.nghiQuyetId))
  const theoId = new Map(danhSach?.map((muc) => [muc.nghiQuyetId, muc]))
  for (const muc of mucKhop ?? []) {
    let cha = muc.nghiQuyetChaId
    while (theoId.has(cha) && !idHienThi.has(cha)) {
      idHienThi.add(cha)
      cha = theoId.get(cha).nghiQuyetChaId
    }
  }
  const hienThi = danhSach?.filter((muc) => idHienThi.has(muc.nghiQuyetId))
  const tieuDeForm = mucSua?.yeuCauSua ? 'Yêu cầu chỉnh sửa' : mucSua?.nghiQuyetId
    ? 'Cập nhật nghị quyết' : mucSua?.nghiQuyetChaId ? 'Thêm nghị quyết cấp dưới' : 'Tạo nghị quyết'

  function moForm(muc) {
    setLoi('')
    setMucSua(muc)
  }

  useEffect(() => {
    const controller = new AbortController()
    goiNghiQuyet(cuocHopId, token, '', { signal: controller.signal }).then((duLieu) => {
      if (!controller.signal.aborted) {
        setDanhSach(duLieu)
      }
    })
      .catch((error) => { if (!controller.signal.aborted) setLoi(error.message) })
    return () => controller.abort()
  }, [cuocHopId, token, phien, lanTai])

  async function gui(duongDan, duLieu, method = 'POST') {
    setDangGui(true)
    setLoi('')
    try {
      await goiNghiQuyet(cuocHopId, token, duongDan, duLieu instanceof FormData ? { method, body: duLieu } : { method, duLieu })
      setLanTai((lan) => lan + 1)
      setMucSua(null)
    } catch (error) {
      setLoi(error.message)
    } finally {
      setDangGui(false)
    }
  }

  function luu(event) {
    event.preventDefault()
    const duLieu = new FormData(event.currentTarget)
    if (mucSua.yeuCauSua) {
      gui(`/${mucSua.nghiQuyetId}/yeu-cau-sua`, { lyDo: duLieu.get('lyDo') })
      return
    }
    if (mucSua.nghiQuyetChaId != null) duLieu.set('nghiQuyetChaId', String(mucSua.nghiQuyetChaId))
    if (mucSua.taiLieuId && !duLieu.get('tep')?.size) duLieu.delete('tep')
    gui(mucSua.nghiQuyetId ? `/${mucSua.nghiQuyetId}` : '', duLieu, mucSua.nghiQuyetId ? 'PUT' : 'POST')
  }

  return (
    <section className="space-y-4 text-sm">
      <div className={LOP_THE + ' space-y-4'}>
        <TieuDeHoSo Icon={FileSignature} cap={hienTieuDe ? 'h2' : 'h3'}
          thaoTac={duocQuanLy && <NutTaoHoSo disabled={dangGui} onClick={() => moForm({})}>Tạo nghị quyết</NutTaoHoSo>}>
          Danh sách nghị quyết
        </TieuDeHoSo>
        {danhSach?.length > 0 && <LocHoSo tuKhoa={tuKhoa} onTuKhoa={setTuKhoa} nhanTim="Tìm nghị quyết"
          boLoc={boLoc} onBoLoc={setBoLoc} cacMuc={BO_LOC} />}
        {loi && !mucSua && <p role="alert" className="text-red-800 phong-toi:text-phong-do">{loi}</p>}
        {!danhSach && !loi && <p role="status" className={LOP_CHU_PHU}>Đang tải nghị quyết…</p>}
        {hienThi?.length === 0 && <p role="status" className={LOP_CHU_PHU}>
          {danhSach.length ? 'Không tìm thấy nghị quyết phù hợp.' : 'Chưa có nghị quyết.'}
        </p>}
      </div>
      {mucSua && (
        <HopThoaiHoSo tieuDe={tieuDeForm} Icon={FileSignature} dangLuu={dangGui} onDong={() => moForm(null)}>
          <form key={JSON.stringify(mucSua)} className="space-y-3 text-sm" onSubmit={luu}>
            {mucSua.yeuCauSua ? (
              <label className="block">Lý do chỉnh sửa<textarea className={TRUONG} name="lyDo" required maxLength={500} /></label>
            ) : (
              <>
                <label className="block">Tiêu đề<input className={TRUONG} name="tieuDe" required maxLength={255} defaultValue={mucSua.tieuDe} /></label>
                <label className="block">Tệp nghị quyết (PDF)<input className={TRUONG} name="tep" type="file" required={!mucSua.taiLieuId} accept=".pdf,application/pdf" /></label>
              </>
            )}
            {loi && <p role="alert" className="text-rose-800 phong-toi:text-phong-do">{loi}</p>}
            <div className="flex flex-wrap justify-end gap-2 pt-2">
              <button className={NUT} disabled={dangGui} type="button" onClick={() => moForm(null)}>Hủy</button>
              <button className={LOP_NUT_CHINH} disabled={dangGui}>{mucSua.yeuCauSua ? 'Gửi yêu cầu chỉnh sửa' : mucSua.nghiQuyetId ? 'Lưu cập nhật' : 'Tạo bản nháp'}</button>
            </div>
          </form>
        </HopThoaiHoSo>
      )}
      {hienThi?.length > 0 && <CayNghiQuyet danhSach={hienThi}>{(muc) => (
        <>
          {muc.taiLieuId && <TepHoSo key={muc.taiLieuId} xemRong token={token} ten={muc.tieuDe + '.pdf'}
            duongDan={`/api/cuoc-hop/${cuocHopId}/nghi-quyet/${muc.nghiQuyetId}/tep`} />}
          {duocQuanLy && <div className="flex flex-wrap gap-2">
            {muc.trangThai === 'NHAP' && muc.taiLieuId && !muc.ngayCongBo
              && <button className={LOP_NUT_CHINH} type="button" disabled={dangGui}
                onClick={() => gui(`/${muc.nghiQuyetId}/cong-bo`)}>Công bố cho thành viên</button>}
            {muc.cap < 4 && <button type="button" className={NUT}
              onClick={() => moForm({ nghiQuyetChaId: muc.nghiQuyetId })}>Thêm nghị quyết cấp dưới</button>}
            {(muc.trangThai === 'YEU_CAU_CHINH_SUA' || (muc.trangThai === 'NHAP' && !muc.ngayCongBo)) && (
              <button className={NUT} type="button" disabled={dangGui} onClick={() => moForm(muc)}>Cập nhật</button>
            )}
            {muc.coTheXoa && (
              <button className={LOP_NUT_XOA} type="button" disabled={dangGui}
                onClick={() => {
                  if (window.confirm(`Xóa bản nháp “${muc.tieuDe}”?`)) gui(`/${muc.nghiQuyetId}`, undefined, 'DELETE')
                }}>Xóa bản nháp</button>
            )}
            {['KHONG_THONG_QUA', 'CHUA_DAT_DA_SO'].includes(muc.trangThai) && <button className={NUT} type="button"
              onClick={() => moForm({ ...muc, yeuCauSua: true })}>Yêu cầu chỉnh sửa</button>}
          </div>}
          {muc.ngayCongBo && (
            <>
              <button type="button" className={NUT} aria-expanded={nghiQuyetDangBieuQuyet === muc.nghiQuyetId}
                onClick={() => setNghiQuyetDangBieuQuyet((id) => id === muc.nghiQuyetId ? null : muc.nghiQuyetId)}>
                {nghiQuyetDangBieuQuyet === muc.nghiQuyetId ? 'Ẩn biểu quyết' : chuTri ? 'Quản lý biểu quyết' : 'Bỏ phiếu và xem kết quả'}
              </button>
              {nghiQuyetDangBieuQuyet === muc.nghiQuyetId && (
                <div className="space-y-4 rounded-lg border border-slate-200 phong-toi:border-phong-vien p-3">
                  {chuTri && <TaoBieuQuyet nguoiDung={chuTri} cuocHopId={cuocHopId} nghiQuyetGoc={muc} />}
                  <BieuQuyetThamDu token={token} cuocHopId={cuocHopId} nghiQuyetId={muc.nghiQuyetId} />
                </div>
              )}
            </>
          )}
        </>
      )}</CayNghiQuyet>}
    </section>
  )
}
