import { useEffect, useState } from 'react'
import RefreshCw from 'lucide-react/dist/esm/icons/refresh-cw.mjs'
import ShieldCheck from 'lucide-react/dist/esm/icons/shield-check.mjs'
import ShieldOff from 'lucide-react/dist/esm/icons/shield-off.mjs'
import { goiQuyenVanPhong } from '../../api/nhomLienPhongApi.js'
import { khopTuKhoa } from '../../components/giaoDienPhongHop.js'
import { DINH_DANG_SO } from './danhMucQuanTri.js'
import { NganChinhSua, PhanHoi, TieuDeTrang, TimKiem, TrangThai } from './uiQuanTri.jsx'

const BO_LOC = [
  ['tat-ca', 'Tất cả'],
  ['da-cap', 'Đã cấp quyền'],
  ['chua-cap', 'Chưa cấp quyền'],
]

export default function ThuKyVanPhong({ token }) {
  const [danhSach, setDanhSach] = useState(null)
  const [tuKhoa, setTuKhoa] = useState('')
  const [loc, setLoc] = useState('tat-ca')
  const [lanTai, setLanTai] = useState(0)
  const [dangTai, setDangTai] = useState(true)
  const [loiTai, setLoiTai] = useState('')
  const [loiLuu, setLoiLuu] = useState('')
  const [thongBao, setThongBao] = useState('')
  const [dangLuu, setDangLuu] = useState(false)
  const [taiKhoanChon, setTaiKhoanChon] = useState(null)

  useEffect(() => {
    const controller = new AbortController()
    goiQuyenVanPhong('', token, { signal: controller.signal })
      .then((duLieu) => {
        if (controller.signal.aborted) return
        setDanhSach(duLieu)
        setLoiTai('')
      })
      .catch((error) => {
        if (!controller.signal.aborted) setLoiTai(error.message)
      })
      .finally(() => {
        if (!controller.signal.aborted) setDangTai(false)
      })
    return () => controller.abort()
  }, [token, lanTai])

  const tong = danhSach?.length ?? 0
  const daCap = danhSach?.filter((nguoi) => nguoi.vanPhong).length ?? 0
  const soLuongTheoLoc = { 'tat-ca': tong, 'da-cap': daCap, 'chua-cap': tong - daCap }
  const ketQua = danhSach?.filter((nguoi) =>
    (loc === 'tat-ca' || nguoi.vanPhong === (loc === 'da-cap')) &&
    [nguoi.hoTen, nguoi.email, nguoi.tenPhongBan].some((giaTri) => khopTuKhoa(giaTri, tuKhoa)),
  )
  const capQuyen = Boolean(taiKhoanChon && !taiKhoanChon.vanPhong)

  function taiLai() {
    setDangTai(true)
    setLoiTai('')
    setLanTai((lan) => lan + 1)
  }

  function chonTaiKhoan(nguoi) {
    setTaiKhoanChon(nguoi)
    setLoiLuu('')
    setThongBao('')
  }

  async function capNhat(event) {
    event.preventDefault()
    if (dangLuu || !taiKhoanChon) return

    const { nguoiDungId, hoTen } = taiKhoanChon
    setDangLuu(true)
    setLoiLuu('')
    try {
      await goiQuyenVanPhong(`/${nguoiDungId}`, token, { method: 'PUT', duLieu: { vanPhong: capQuyen } })
      setDanhSach((ds) => ds.map((muc) => muc.nguoiDungId === nguoiDungId ? { ...muc, vanPhong: capQuyen } : muc))
      setTaiKhoanChon(null)
      setThongBao(capQuyen ? `Đã cấp quyền Văn phòng cho ${hoTen}.` : `Đã thu hồi quyền Văn phòng của ${hoTen}.`)
      taiLai()
    } catch (error) {
      setLoiLuu(error.message)
    } finally {
      setDangLuu(false)
    }
  }

  return (
    <section className="adm-page">
      <TieuDeTrang ten="Quyền Văn phòng"
        moTa="Quản lý thư ký được điều phối nhóm liên phòng và duyệt kế hoạch họp thường khi được giao.">
        <button type="button" className="adm-btn adm-secondary" onClick={taiLai} disabled={dangTai || dangLuu}>
          <RefreshCw size={16} aria-hidden="true" /> {dangTai ? 'Đang tải…' : 'Làm mới'}
        </button>
      </TieuDeTrang>

      <PhanHoi loi={loiTai} thongBao={thongBao} onTaiLai={taiLai} />
      {!danhSach && dangTai && <p role="status" className="adm-surface adm-empty">Đang tải tài khoản thư ký…</p>}

      {danhSach && <section className="adm-surface" aria-label="Danh sách tài khoản thư ký">
        <div className="adm-section-heading"><h2>Tài khoản thư ký</h2></div>
        <div className="adm-toolbar adm-office-toolbar">
          <div className="adm-segments" role="group" aria-label="Lọc quyền Văn phòng">
            {BO_LOC.map(([ma, ten]) => <button type="button" key={ma} aria-pressed={loc === ma} onClick={() => setLoc(ma)}>
              {ten}<span aria-label={`${soLuongTheoLoc[ma]} tài khoản`}>{DINH_DANG_SO.format(soLuongTheoLoc[ma])}</span>
            </button>)}
          </div>
          <TimKiem tuKhoa={tuKhoa} onDoi={setTuKhoa} nhan="Tìm tên, email hoặc phòng ban" />
        </div>

        {ketQua.length ? <div className="adm-table-scroll" role="region" aria-label="Bảng quyền Văn phòng" tabIndex={0}>
          <table className="adm-table adm-office-table" role="table">
            <thead role="rowgroup"><tr role="row">
              <th role="columnheader" scope="col">Thư ký</th>
              <th role="columnheader" scope="col">Phòng ban</th>
              <th role="columnheader" scope="col">Quyền Văn phòng</th>
              <th role="columnheader" scope="col" className="adm-actions">Thao tác</th>
            </tr></thead>
            <tbody role="rowgroup">{ketQua.map((nguoi) => (
              <tr role="row" key={nguoi.nguoiDungId}>
                <th role="rowheader" scope="row">
                  <span className="adm-assignee">
                    <span className="adm-mini-avatar" aria-hidden="true">
                      {nguoi.hoTen?.trim().split(/\s+/).at(-1)?.[0]?.toLocaleUpperCase('vi')}
                    </span>
                    <span className="adm-office-identity">
                      <strong>{nguoi.hoTen}</strong>
                      <small>{nguoi.email || `Tài khoản #${nguoi.nguoiDungId}`}</small>
                    </span>
                  </span>
                </th>
                <td role="cell" className="adm-office-dept">{nguoi.tenPhongBan || 'Chưa gán phòng ban'}</td>
                <td role="cell" className="adm-office-status"><TrangThai ten={nguoi.vanPhong ? 'Đã cấp' : 'Chưa cấp'} mau={nguoi.vanPhong ? 'xanh' : 'xam'} /></td>
                <td role="cell" className="adm-actions">
                  <button type="button" className={`adm-btn ${nguoi.vanPhong ? 'adm-secondary adm-danger-text' : 'adm-primary'}`}
                    disabled={dangLuu || dangTai || Boolean(loiTai)} onClick={() => chonTaiKhoan(nguoi)}
                    aria-label={`${nguoi.vanPhong ? 'Thu hồi quyền của' : 'Cấp quyền cho'} ${nguoi.hoTen}`}>
                    {nguoi.vanPhong ? 'Thu hồi quyền' : 'Cấp quyền'}
                  </button>
                </td>
              </tr>
            ))}</tbody>
          </table>
        </div> : <p className="adm-empty">{tong ? 'Không có tài khoản phù hợp với bộ lọc.' : 'Chưa có tài khoản thư ký đang hoạt động.'}</p>}
      </section>}

      {taiKhoanChon && <NganChinhSua oGiua tieuDe={capQuyen ? 'Cấp quyền Văn phòng' : 'Thu hồi quyền Văn phòng'}
        moTa={[taiKhoanChon.hoTen, taiKhoanChon.email || taiKhoanChon.tenPhongBan].filter(Boolean).join(' · ')}
        dangLuu={dangLuu} onDong={() => chonTaiKhoan(null)}>
        <form className="adm-form" onSubmit={capNhat}>
          <div className="adm-permission-impact" data-thu-hoi={!capQuyen}>
            {capQuyen ? <ShieldCheck size={20} aria-hidden="true" /> : <ShieldOff size={20} aria-hidden="true" />}
            <p>{capQuyen
              ? 'Tài khoản có thể điều phối nhóm liên phòng và duyệt kế hoạch họp thường được giao.'
              : 'Tài khoản sẽ mất quyền điều phối nhóm và quyền xử lý các kế hoạch đang chờ tài khoản này duyệt với tư cách Văn phòng.'}</p>
          </div>
          <PhanHoi loi={loiLuu} />
          <div className="adm-form-actions">
            <button type="button" autoFocus className="adm-btn adm-secondary" onClick={() => chonTaiKhoan(null)} disabled={dangLuu}>Hủy</button>
            <button type="submit" className={`adm-btn ${capQuyen ? 'adm-primary' : 'adm-danger'}`} disabled={dangLuu}>
              {dangLuu ? 'Đang lưu…' : capQuyen ? 'Xác nhận cấp quyền' : 'Xác nhận thu hồi'}
            </button>
          </div>
        </form>
      </NganChinhSua>}
    </section>
  )
}
