import { useEffect, useState } from 'react'
import ArrowRight from 'lucide-react/dist/esm/icons/arrow-right.mjs'
import Building2 from 'lucide-react/dist/esm/icons/building-2.mjs'
import DoorOpen from 'lucide-react/dist/esm/icons/door-open.mjs'
import Monitor from 'lucide-react/dist/esm/icons/monitor.mjs'
import UserRoundCog from 'lucide-react/dist/esm/icons/user-round-cog.mjs'
import Users from 'lucide-react/dist/esm/icons/users.mjs'
import { goiApiQuanTri } from '../../api/quanTriApi.js'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import { DINH_DANG_SO, TINH_TRANG_THIET_BI, TRANG_THAI_PHONG } from './danhMucQuanTri.js'
import { PhanHoi, TieuDeTrang, TrangThai } from './uiQuanTri.jsx'

const DANH_MUC = [
  { id: 'users', ten: 'Tài khoản', BieuTuong: Users, mau: 'tim' },
  { id: 'departments', ten: 'Phòng ban', BieuTuong: Building2, mau: 'lam' },
  { id: 'meeting-rooms', ten: 'Phòng họp', BieuTuong: DoorOpen, mau: 'xanh' },
  { id: 'equipment', ten: 'Thiết bị', BieuTuong: Monitor, mau: 'vang' },
]

const LOAI_CO_SO = [
  { id: 'meeting-rooms', ten: 'Phòng họp', khoa: 'phongHopId', tenMuc: 'tenPhongHop', trangThai: 'trangThai', bang: TRANG_THAI_PHONG, sanSang: 'SAN_SANG' },
  { id: 'equipment', ten: 'Thiết bị', khoa: 'thietBiId', tenMuc: 'tenThietBi', trangThai: 'tinhTrang', bang: TINH_TRANG_THIET_BI, sanSang: 'TOT' },
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
      <TieuDeTrang ten="Bảng điều khiển" moTa="Theo dõi tổ chức và cơ sở vật chất phục vụ cuộc họp.">
        <time className="adm-date">{ngay}</time>
      </TieuDeTrang>
      <PhanHoi loi={loi} onTaiLai={() => setLanTai((lan) => lan + 1)} />

      <div className="adm-stat-grid">
        {DANH_MUC.map(({ id, ten, BieuTuong, mau }) => (
          <button type="button" className="adm-stat" key={id} onClick={() => onChonMuc(id)} aria-label={`Quản lý ${ten.toLocaleLowerCase('vi')}`}>
            <span className="adm-stat-top">
              <span className="adm-tile-icon" data-mau={mau}><BieuTuong size={21} aria-hidden="true" /></span>
              <ArrowRight size={16} aria-hidden="true" />
            </span>
            <span className="adm-stat-value">{duLieu[id] === undefined ? '—' : DINH_DANG_SO.format(duLieu[id].length)}</span>
            <span className="adm-stat-label">{ten}</span>
          </button>
        ))}
      </div>

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

      <button type="button" className="adm-shortcut adm-surface" onClick={() => onChonMuc('phan-cong')}>
        <span className="adm-tile-icon" data-mau="tim"><UserRoundCog size={21} aria-hidden="true" /></span>
        <span><strong>Phân công thư ký</strong><small>Quản lý thư ký mặc định của lãnh đạo và trưởng phòng.</small></span>
        <ArrowRight size={18} aria-hidden="true" />
      </button>
    </div>
  )
}
