import { useEffect, useState } from 'react'
import ArrowRight from 'lucide-react/dist/esm/icons/arrow-right.mjs'
import CalendarDays from 'lucide-react/dist/esm/icons/calendar-days.mjs'
import ShieldCheck from 'lucide-react/dist/esm/icons/shield-check.mjs'
import Building2 from 'lucide-react/dist/esm/icons/building-2.mjs'
import DoorOpen from 'lucide-react/dist/esm/icons/door-open.mjs'
import Monitor from 'lucide-react/dist/esm/icons/monitor.mjs'
import UserRoundCog from 'lucide-react/dist/esm/icons/user-round-cog.mjs'
import Users from 'lucide-react/dist/esm/icons/users.mjs'
import { goiApiQuanTri } from '../../api/quanTriApi.js'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import { DINH_DANG_SO, TINH_TRANG_THIET_BI, TRANG_THAI_PHONG, TRANG_THAI_TAI_KHOAN } from './danhMucQuanTri.js'
import { PhanHoi, TrangThai } from './uiQuanTri.jsx'

const DANH_MUC = [
  { id: 'users', ten: 'Tài khoản', BieuTuong: Users, mau: 'lam', trangThai: 'trangThai', ma: 'HOAT_DONG', nhan: TRANG_THAI_TAI_KHOAN.HOAT_DONG[0] },
  { id: 'departments', ten: 'Phòng ban', BieuTuong: Building2, mau: 'tim', trangThai: 'trangThai', ma: 'HOAT_DONG', nhan: 'Hoạt động' },
  { id: 'meeting-rooms', ten: 'Phòng họp', BieuTuong: DoorOpen, mau: 'xanh', trangThai: 'trangThai', ma: 'SAN_SANG', nhan: 'Sẵn sàng' },
  { id: 'equipment', ten: 'Thiết bị', BieuTuong: Monitor, mau: 'vang', trangThai: 'tinhTrang', ma: 'TOT', nhan: 'Tình trạng tốt' },
]

const LOAI_CO_SO = [
  { id: 'meeting-rooms', ten: 'Phòng họp', khoa: 'phongHopId', tenMuc: 'tenPhongHop', trangThai: 'trangThai', bang: TRANG_THAI_PHONG, sanSang: 'SAN_SANG' },
  { id: 'equipment', ten: 'Thiết bị', khoa: 'thietBiId', tenMuc: 'tenThietBi', trangThai: 'tinhTrang', bang: TINH_TRANG_THIET_BI, sanSang: 'TOT' },
]

const TAC_VU = [
  { id: 'thu-ky-van-phong', ten: 'Quyền Văn phòng', moTa: 'Cấp hoặc thu hồi quyền điều phối nhóm.', BieuTuong: ShieldCheck, mau: 'xanh' },
  { id: 'phan-cong', ten: 'Phân công thư ký', moTa: 'Gán thư ký mặc định cho lãnh đạo và trưởng phòng.', BieuTuong: UserRoundCog, mau: 'lam' },
]

const DINH_DANG_NGAY = new Intl.DateTimeFormat('vi-VN', {
  weekday: 'long', day: '2-digit', month: 'long', year: 'numeric',
})

export default function TongQuanQuanTri({ token, onChonMuc }) {
  const [duLieu, setDuLieu] = useState({})
  const [loi, setLoi] = useState('')
  const [lanTai, setLanTai] = useState(0)
  const [ngay] = useState(() => DINH_DANG_NGAY.format(new Date()))
  const phienDongBo = useDongBoDuLieu()

  useEffect(() => {
    const controller = new AbortController()
    Promise.allSettled(DANH_MUC.map(({ id }) => goiApiQuanTri(id, token, { signal: controller.signal })))
      .then((ketQua) => {
        if (controller.signal.aborted) return
        const moi = {}
        const loiTai = []
        ketQua.forEach((muc, index) => {
          const { id, ten } = DANH_MUC[index]
          if (muc.status === 'fulfilled') moi[id] = muc.value
          else loiTai.push(ten.toLocaleLowerCase('vi'))
        })
        setDuLieu((cu) => ({ ...cu, ...moi }))
        setLoi(loiTai.length ? `Chưa tải được ${loiTai.join(', ')}.` : '')
      })
    return () => controller.abort()
  }, [token, lanTai, phienDongBo])

  return (
    <div className="adm-page">
      <header className="adm-overview-hero">
        <div>
          <time className="adm-date"><CalendarDays size={16} aria-hidden="true" />{ngay}</time>
          <h1>Bảng điều khiển</h1>
          <p>Theo dõi nhân sự, phòng họp và thiết bị.</p>
        </div>
        <button type="button" className="adm-btn adm-hero-action" onClick={() => onChonMuc('cuoc-hop')}>
          Hoạt động cuộc họp <ArrowRight size={17} aria-hidden="true" />
        </button>
      </header>
      <PhanHoi loi={loi} onTaiLai={() => setLanTai((lan) => lan + 1)} />

      <section className="adm-stat-grid" aria-label="Thống kê danh mục quản trị">
        {DANH_MUC.map(({ id, ten, BieuTuong, mau, trangThai, ma, nhan }) => {
          const danhSach = duLieu[id]
          const soLuong = danhSach?.filter((muc) => muc[trangThai] === ma).length
          return (
            <button type="button" className="adm-stat" key={id} onClick={() => onChonMuc(id)}
              aria-label={`Quản lý ${ten.toLocaleLowerCase('vi')}`}>
              <span className="adm-stat-top">
                <span className="adm-tile-icon" data-mau={mau}><BieuTuong size={20} aria-hidden="true" /></span>
                <ArrowRight size={17} aria-hidden="true" />
              </span>
              <span className="adm-stat-label">{ten}</span>
              <span className="adm-stat-value">{danhSach ? DINH_DANG_SO.format(danhSach.length) : '—'}</span>
              <span className="adm-stat-note">{danhSach ? `${DINH_DANG_SO.format(soLuong)} ${nhan.toLocaleLowerCase('vi')}` : 'Chưa có số liệu'}</span>
            </button>
          )
        })}
      </section>

      <div className="adm-overview-grid">
        <section className="adm-surface adm-section">
          <div className="adm-section-heading"><h2>Tình trạng cơ sở vật chất</h2></div>
          <div className="adm-facilities">
            {LOAI_CO_SO.map(({ id, ten, khoa, tenMuc, trangThai, bang, sanSang }) => {
              const danhSach = duLieu[id]
              const soSanSang = danhSach?.filter((muc) => muc[trangThai] === sanSang).length ?? 0
              const canTheoDoi = danhSach?.filter((muc) => muc[trangThai] !== sanSang) ?? []
              const tyLe = danhSach?.length ? soSanSang / danhSach.length * 100 : 0
              return (
                <div key={id} className="adm-facility">
                  <div className="adm-facility-title">
                    <h3>{ten}</h3>
                    <button type="button" className="adm-link" onClick={() => onChonMuc(id)}>Xem danh sách <ArrowRight size={14} /></button>
                  </div>
                  {danhSach ? (
                    <>
                      <div className="adm-progress" role="meter" aria-label={`${ten} - ${bang[sanSang][0]}`} aria-valuemin={0}
                        aria-valuemax={100} aria-valuenow={Math.round(tyLe)}
                        aria-valuetext={`${soSanSang} trên ${danhSach.length}`}>
                        <span style={{ width: `${tyLe}%` }} />
                      </div>
                      <p className="adm-progress-caption">{soSanSang}/{danhSach.length} {bang[sanSang][0].toLocaleLowerCase('vi')}</p>
                      {canTheoDoi.length > 0 ? <ul className="adm-watch-list">
                        {canTheoDoi.slice(0, 3).map((muc) => {
                          const [nhan, mau] = bang[muc[trangThai]] ?? [muc[trangThai] || 'Chưa cập nhật']
                          return <li key={muc[khoa]}><span>{muc[tenMuc]}</span><TrangThai ten={nhan} mau={mau} /></li>
                        })}
                      </ul> : <p className="adm-muted">{danhSach.length ? 'Không có mục cần theo dõi.' : 'Chưa có dữ liệu.'}</p>}
                    </>
                  ) : <p className="adm-muted">{loi ? 'Dữ liệu chưa có sẵn.' : 'Đang tải…'}</p>}
                </div>
              )
            })}
          </div>
        </section>

        <section className="adm-surface adm-admin-actions">
          <div className="adm-section-heading"><h2>Điều phối nhân sự</h2></div>
          {TAC_VU.map(({ id, ten, moTa, BieuTuong, mau }) => (
            <button key={id} type="button" className="adm-shortcut" onClick={() => onChonMuc(id)}>
              <span className="adm-tile-icon" data-mau={mau}><BieuTuong size={20} aria-hidden="true" /></span>
              <span><strong>{ten}</strong><small>{moTa}</small></span>
              <ArrowRight size={17} aria-hidden="true" />
            </button>
          ))}
        </section>
      </div>
    </div>
  )
}
