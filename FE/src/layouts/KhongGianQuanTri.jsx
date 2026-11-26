import { useState } from 'react'
import { useKetNoiHoSo } from '../services/useKetNoiHoSo.js'
import Building2 from 'lucide-react/dist/esm/icons/building-2.mjs'
import CalendarDays from 'lucide-react/dist/esm/icons/calendar-days.mjs'
import DoorOpen from 'lucide-react/dist/esm/icons/door-open.mjs'
import LayoutDashboard from 'lucide-react/dist/esm/icons/layout-dashboard.mjs'
import LogOut from 'lucide-react/dist/esm/icons/log-out.mjs'
import Menu from 'lucide-react/dist/esm/icons/menu.mjs'
import Monitor from 'lucide-react/dist/esm/icons/monitor.mjs'
import UserRoundCog from 'lucide-react/dist/esm/icons/user-round-cog.mjs'
import Users from 'lucide-react/dist/esm/icons/users.mjs'
import X from 'lucide-react/dist/esm/icons/x.mjs'
import ChiTietDeXuat from '../pages/thuky/ChiTietDeXuat.jsx'
import TongQuanCuocHop from '../pages/TongQuanCuocHop.jsx'
import TongQuanQuanTri from '../pages/quantri/TongQuanQuanTri.jsx'
import DanhMucQuanTri from '../pages/quantri/DanhMucQuanTri.jsx'
import PhanCongThuKy from '../pages/quantri/PhanCongThuKy.jsx'
import { PhanHoi, TieuDeTrang } from '../pages/quantri/uiQuanTri.jsx'
import './quanTri.css'

const NHOM_MENU = [
  { ten: 'Tổng quan', muc: [
    { id: 'tong-quan', ten: 'Bảng điều khiển', BieuTuong: LayoutDashboard },
    { id: 'cuoc-hop', ten: 'Hoạt động cuộc họp', BieuTuong: CalendarDays },
  ] },
  { ten: 'Tổ chức', muc: [
    { id: 'users', ten: 'Tài khoản', BieuTuong: Users },
    { id: 'departments', ten: 'Phòng ban', BieuTuong: Building2 },
    { id: 'phan-cong', ten: 'Phân công thư ký', BieuTuong: UserRoundCog },
  ] },
  { ten: 'Cơ sở vật chất', muc: [
    { id: 'meeting-rooms', ten: 'Phòng họp', BieuTuong: DoorOpen },
    { id: 'equipment', ten: 'Thiết bị', BieuTuong: Monitor },
  ] },
]
const CAC_MUC = NHOM_MENU.flatMap(({ muc }) => muc)

export default function KhongGianQuanTri({ nguoiDung, loi, onDangXuat }) {
  const [muc, setMuc] = useState('tong-quan')
  const [phongHopLoc, setPhongHopLoc] = useState(null)
  const [moMenu, setMoMenu] = useState(false)
  const [hoSo, setHoSo] = useState(null)
  const { ten: tenMuc, BieuTuong: BieuTuongMuc } = CAC_MUC.find(({ id }) => id === muc)
  const { token, hoTen, nguoiDungId } = nguoiDung
  useKetNoiHoSo(token)
  const chuDau = hoTen?.trim().split(/\s+/).slice(-2).map((tu) => tu[0]).join('').toLocaleUpperCase('vi') || 'QT'

  function chonMuc(id, phongHopId = null) {
    setPhongHopLoc(phongHopId)
    setHoSo(null)
    setMuc(id)
    setMoMenu(false)
    window.scrollTo({ top: 0, behavior: 'instant' })
  }

  return (
    <div className="adm-shell">
      <aside className="adm-sidebar">
        <div className="adm-brand">
          <div><strong>Họp hành chính</strong><span>Không gian quản trị</span></div>
          <button type="button" className="adm-icon-btn adm-menu-toggle"
            aria-label={moMenu ? 'Đóng điều hướng' : 'Mở điều hướng'} aria-controls="adm-navigation"
            aria-expanded={moMenu} onClick={() => setMoMenu(!moMenu)}>
            {moMenu ? <X size={20} /> : <Menu size={20} />}
          </button>
        </div>
        <nav id="adm-navigation" aria-label="Quản trị" className="adm-nav" data-open={moMenu}>
          {NHOM_MENU.map(({ ten, muc: danhSach }) => (
            <div key={ten} className="adm-nav-group">
              <p>{ten}</p>
              {danhSach.map(({ id, ten: nhan, BieuTuong }) => (
                <button type="button" key={id} aria-current={muc === id ? 'page' : undefined} onClick={() => chonMuc(id)}>
                  <BieuTuong size={18} aria-hidden="true" />{nhan}
                </button>
              ))}
            </div>
          ))}
        </nav>
      </aside>

      <div className="adm-workspace">
        <header className="adm-topbar">
          <p><BieuTuongMuc size={17} aria-hidden="true" /><span>Quản trị</span><span aria-hidden="true">/</span><strong>{tenMuc}</strong></p>
          <div className="adm-profile">
            <span className="adm-avatar" aria-hidden="true">{chuDau}</span>
            <div><strong>{hoTen || 'Quản trị viên'}</strong><span>Quản trị viên</span></div>
            <button type="button" className="adm-icon-btn" onClick={onDangXuat} aria-label="Đăng xuất" title="Đăng xuất">
              <LogOut aria-hidden="true" size={18} />
            </button>
          </div>
        </header>
        <main className="adm-main">
          <PhanHoi loi={loi} />
          {muc === 'tong-quan' ? <TongQuanQuanTri token={token} onChonMuc={chonMuc} />
            : muc === 'cuoc-hop' ? hoSo ? (
              <ChiTietDeXuat key={hoSo} cuocHopId={hoSo} token={token} nguoiDung={nguoiDung}
                chiXem tabBanDau="thong-tin" onQuayLai={() => setHoSo(null)} />
            ) : <section className="adm-page">
              <TieuDeTrang ten="Hoạt động cuộc họp" />
              <TongQuanCuocHop token={token} nguoiDung={nguoiDung} onXemCuocHop={setHoSo} />
            </section>
                : muc === 'phan-cong' ? <PhanCongThuKy token={token} />
                : <DanhMucQuanTri key={muc} token={token} loai={muc} nguoiDungId={nguoiDungId} phongHopLoc={phongHopLoc}
                  onXemThietBi={(phongHopId) => chonMuc('equipment', phongHopId)} />}
        </main>
      </div>
    </div>
  )
}
