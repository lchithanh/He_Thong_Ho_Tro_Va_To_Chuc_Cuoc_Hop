import { useEffect, useState } from 'react'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import { LOP_KHOI, LOP_TRUONG, LOP_NUT_CHINH } from '../../components/lopBieuMau.js'
import {
  guiDeXuatCuocHop,
  layDanhSachDeXuat,
  layTrangThaiHienThiCuocHop,
  taoDeXuatCuocHop,
} from '../../api/cuocHopApi.js'

const PHAM_VI = {
  NOI_BO: 'Nội bộ',
  LIEN_PHONG_BAN: 'Liên phòng ban',
  TOAN_CO_QUAN: 'Toàn cơ quan',
  BEN_NGOAI: 'Bên ngoài',
}
const TEN_TRANG_THAI = {
  NHAP: 'Bản nháp',
  CHO_TIEP_NHAN: 'Chờ tiếp nhận',
  DANG_CHUAN_BI: 'Đang chuẩn bị',
  CHO_DUYET: 'Chờ duyệt',
  DA_DUYET: 'Đã duyệt',
  TRA_LAI: 'Đã trả lại',
  TU_CHOI: 'Đã từ chối',
  DANG_DIEN_RA: 'Đang diễn ra',
  DA_KET_THUC: 'Đã kết thúc',
  DA_HUY: 'Đã hủy',
}

export default function DeXuatCuaToi({ token, nguoiDung, onXemCuocHop }) {
  const phienDongBo = useDongBoDuLieu()
  const [danhSach, setDanhSach] = useState(null)
  const [lanTai, setLanTai] = useState(0)
  const [loiTai, setLoiTai] = useState('')
  const [loi, setLoi] = useState('')
  const [thongBao, setThongBao] = useState('')
  const [dangGui, setDangGui] = useState(false)
  const [phamVi, setPhamVi] = useState('NOI_BO')
  const lanhDao = nguoiDung.maVaiTro === 'LANH_DAO'
  const coThuKy = Boolean(nguoiDung.tenThuKyMacDinh)

  useEffect(() => {
    const controller = new AbortController()
    layDanhSachDeXuat(token, controller.signal)
      .then((duLieu) => {
        if (controller.signal.aborted) return
        setDanhSach(duLieu.filter((muc) => muc.nguoiDeXuatId === nguoiDung.nguoiDungId))
        setLoiTai('')
      })
      .catch((error) => { if (!controller.signal.aborted) setLoiTai(error.message) })
    return () => controller.abort()
  }, [token, nguoiDung.nguoiDungId, lanTai, phienDongBo])

  async function xuLyDeXuat(thaoTac) {
    if (dangGui) return
    setDangGui(true)
    setLoi('')
    setThongBao('')
    try {
      const ketQua = await thaoTac()
      setThongBao(ketQua.message)
      setLanTai((lan) => lan + 1)
    } catch (error) {
      setLoi(error.message)
    } finally {
      setDangGui(false)
    }
  }

  function taoDeXuat(event) {
    event.preventDefault()
    const form = event.currentTarget
    const duLieu = new FormData(form)
    const guiNgay = event.nativeEvent.submitter?.value === 'gui'
    if (guiNgay && !coThuKy) return
    xuLyDeXuat(async () => {
      const ketQua = await taoDeXuatCuocHop({
        tieuDe: duLieu.get('tieuDe').trim(),
        mucTieu: duLieu.get('mucTieu').trim(),
        noiDungDuKien: duLieu.get('noiDungDuKien').trim(),
        loaiHinh: duLieu.get('loaiHinh'),
        phamViCuocHop: phamVi,
        phongBanChuTriId: phamVi === 'NOI_BO'
          ? Number(duLieu.get('phongBanChuTriId') || nguoiDung.phongBanId) || null
          : null,
        coKhachMoiBenNgoai: phamVi === 'BEN_NGOAI' || duLieu.has('coKhachMoiBenNgoai'),
        thuKyCuocHopId: Number(duLieu.get('thuKyCuocHopId')) || null,
        guiNgay,
      }, token)
      form.reset()
      setPhamVi('NOI_BO')
      return ketQua
    })
  }

  return (
    <div className="grid gap-5 xl:grid-cols-2">
      <form className={LOP_KHOI} onSubmit={taoDeXuat}>
        <div>
          <h1 className="text-xl font-bold">Đề xuất cuộc họp</h1>
          <p className="mt-1 text-sm text-slate-600">Thư ký nhận đề xuất · {nguoiDung.tenThuKyMacDinh || 'Chưa được Admin phân công'}</p>
        </div>
        <label className="block text-sm font-semibold">Tiêu đề
          <input className={LOP_TRUONG} name="tieuDe" required maxLength={255} />
        </label>
        <div className="grid gap-3 sm:grid-cols-2">
          <label className="text-sm font-semibold">Phạm vi
            <select className={LOP_TRUONG} value={phamVi} onChange={(event) => setPhamVi(event.target.value)}>
              {Object.entries(PHAM_VI).map(([ma, ten]) => <option key={ma} value={ma}>{ten}</option>)}
            </select>
          </label>
          <label className="text-sm font-semibold">Hình thức
            <select className={LOP_TRUONG} name="loaiHinh">
              <option value="TRUC_TIEP">Trực tiếp</option>
              <option value="TRUC_TUYEN">Trực tuyến</option>
            </select>
          </label>
        </div>
        {phamVi === 'NOI_BO' && (lanhDao ? (
          <label className="block text-sm font-semibold">Mã phòng ban chủ trì
            <input className={LOP_TRUONG} name="phongBanChuTriId" type="number" min="1" step="1" required defaultValue={nguoiDung.phongBanId ?? ''} />
          </label>
        ) : (
          <p className="text-sm text-slate-600">Phòng ban chủ trì · {nguoiDung.tenPhongBan || 'Chưa xác định'}</p>
        ))}
        <label className="block text-sm font-semibold">Mục tiêu
          <textarea className={LOP_TRUONG} name="mucTieu" rows={2} />
        </label>
        <label className="block text-sm font-semibold">Nội dung dự kiến
          <textarea className={LOP_TRUONG} name="noiDungDuKien" rows={3} />
        </label>
        <label className="flex items-center gap-2 text-sm">
          {phamVi === 'BEN_NGOAI'
            ? <input type="checkbox" checked disabled readOnly />
            : <input name="coKhachMoiBenNgoai" type="checkbox" />}
          Có khách mời bên ngoài
        </label>
        <label className="block text-sm font-semibold">Mã cán bộ được đề nghị làm thư ký cuộc họp
          <input className={LOP_TRUONG} name="thuKyCuocHopId" type="number" min="1" step="1"
            placeholder="Để trống nếu dùng thư ký mặc định" />
        </label>
        <div className="flex flex-wrap gap-2">
          <button type="submit" value="nhap" disabled={dangGui} className="rounded-lg border border-slate-300 px-4 py-2 text-sm font-semibold disabled:opacity-50">Lưu nháp</button>
          <button type="submit" value="gui" disabled={dangGui || !coThuKy} className={LOP_NUT_CHINH}>Gửi thư ký</button>
        </div>
        {loi && <p role="alert" className="text-sm text-red-800">{loi}</p>}
        {thongBao && <p role="status" className="text-sm text-emerald-800">{thongBao}</p>}
      </form>
      <section className={LOP_KHOI}>
        <h2 className="text-lg font-bold">Đề xuất của tôi</h2>
        {loiTai ? <p role="alert" className="text-sm text-red-800">{loiTai}</p>
          : danhSach === null ? <p role="status" className="text-sm text-slate-600">Đang tải đề xuất…</p>
            : danhSach.length === 0 && <p className="text-sm text-slate-600">Chưa có đề xuất cuộc họp.</p>}
        <ul className="divide-y divide-slate-200">
          {danhSach?.map((muc) => (
            <li key={muc.cuocHopId} className="flex flex-wrap items-center justify-between gap-3 py-3">
              <div className="min-w-0">
                <p className="break-words text-sm font-semibold">{muc.tieuDe}</p>
                <p className="text-xs text-slate-600">{muc.maCuocHop} · {TEN_TRANG_THAI[layTrangThaiHienThiCuocHop(muc)] ?? muc.trangThai}</p>
              </div>
              <div className="flex flex-wrap gap-2">
                {onXemCuocHop && <button type="button" className="rounded-lg border px-3 py-2 text-sm font-semibold text-emerald-800"
                  onClick={() => onXemCuocHop(muc.cuocHopId)}>Xem chi tiết</button>}
              {muc.trangThai === 'NHAP' && <button type="button" disabled={dangGui || !coThuKy} className={LOP_NUT_CHINH} onClick={() => xuLyDeXuat(() => guiDeXuatCuocHop(muc.cuocHopId, token))}>Gửi thư ký</button>}
              </div>
            </li>
          ))}
        </ul>
      </section>
    </div>
  )
}
