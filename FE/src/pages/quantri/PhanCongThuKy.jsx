import { useEffect, useState } from 'react'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import { PhanHoi, TieuDeTrang, TimKiem, TrangThai } from './uiQuanTri.jsx'
import { huyPhanCongThuKy, layDuLieuPhanCongThuKy, luuPhanCongThuKy } from '../../api/cuocHopApi.js'

export default function PhanCongThuKy({ token }) {
  const phienDongBo = useDongBoDuLieu()
  const [duLieu, setDuLieu] = useState(null)
  const [loi, setLoi] = useState('')
  const [loiTai, setLoiTai] = useState('')
  const [lanTai, setLanTai] = useState(0)
  const [tuKhoa, setTuKhoa] = useState('')
  const [nhom, setNhom] = useState('lanh-dao')
  const [thongBao, setThongBao] = useState('')
  const [dangLuuId, setDangLuuId] = useState(null)

  useEffect(() => {
    const controller = new AbortController()
    layDuLieuPhanCongThuKy(token, controller.signal)
      .then((ketQua) => {
        if (controller.signal.aborted) return
        setDuLieu(ketQua)
        setLoiTai('')
      })
      .catch((error) => { if (!controller.signal.aborted) setLoiTai(error.message) })
    return () => controller.abort()
  }, [token, phienDongBo, lanTai])

  const phongTheoId = new Map(duLieu?.phongBan.map((phong) => [phong.phongBanId, phong]))
  const taiKhoan = duLieu?.nguoiDung.filter((muc) => muc.trangThai === 'HOAT_DONG') ?? []
  const lanhDao = taiKhoan.filter((muc) => muc.maVaiTro === 'LANH_DAO')
  const truongPhong = taiKhoan.filter((muc) =>
    muc.maVaiTro === 'CAN_BO'
    && phongTheoId.get(muc.phongBanId)?.truongPhongId === muc.nguoiDungId,
  )
  const chuSoHuuThuKy = new Map()
  for (const muc of duLieu?.phanCong ?? []) {
    const ids = chuSoHuuThuKy.get(muc.thuKyId) ?? []
    ids.push(muc.nguoiDuocHoTroId)
    chuSoHuuThuKy.set(muc.thuKyId, ids)
  }
  const thuKy = taiKhoan.filter((muc) => muc.maVaiTro === 'THU_KY')

  async function luu(event, nguoiDuocHoTroId) {
    event.preventDefault()
    if (dangLuuId !== null) return
    const huy = event.nativeEvent.submitter?.value === 'huy'
    const thuKyId = Number(new FormData(event.currentTarget).get('thuKyId'))
    if (!huy && thuKyId <= 0) return
    setDangLuuId(nguoiDuocHoTroId)
    setLoi('')
    setThongBao('')
    try {
      const phanCong = huy
        ? null
        : await luuPhanCongThuKy(nguoiDuocHoTroId, thuKyId, token)
      if (huy) await huyPhanCongThuKy(nguoiDuocHoTroId, token)
      setDuLieu((hienTai) => ({
        ...hienTai,
        phanCong: [...hienTai.phanCong.filter((muc) => muc.nguoiDuocHoTroId !== nguoiDuocHoTroId), ...(phanCong ? [phanCong] : [])],
      }))
      setThongBao(phanCong
        ? `Đã phân công ${phanCong.tenThuKy} cho ${phanCong.tenNguoiDuocHoTro}.`
        : 'Đã bỏ phân công thư ký mặc định.')
    } catch (error) {
      setLoi(error.message)
    } finally {
      setDangLuuId(null)
    }
  }

  const tuTim = tuKhoa.trim().toLocaleLowerCase('vi')
  const phanCongTheoId = new Map(duLieu?.phanCong.map((item) => [item.nguoiDuocHoTroId, item]))
  const danhSach = (nhom === 'lanh-dao' ? lanhDao : truongPhong).filter((muc) =>
    [muc.hoTen, muc.tenPhongBan || phongTheoId.get(muc.phongBanId)?.tenPhongBan,
      phanCongTheoId.get(muc.nguoiDungId)?.tenThuKy]
      .some((giaTri) => String(giaTri ?? '').toLocaleLowerCase('vi').includes(tuTim)),
  )

  return (
    <section className="adm-page">
      <TieuDeTrang ten="Phân công thư ký" moTa="Chọn thư ký mặc định cho từng lãnh đạo hoặc trưởng phòng." />
      <PhanHoi loi={loiTai} onTaiLai={() => setLanTai((lan) => lan + 1)} />
      <PhanHoi loi={loi} thongBao={thongBao} />
      {!duLieu && !loiTai && <p role="status" className="adm-empty adm-surface">Đang tải dữ liệu phân công…</p>}
      {duLieu && (
        <section className="adm-surface" aria-label="Danh sách phân công">
          <div className="adm-toolbar">
            <div className="adm-segments" role="group" aria-label="Nhóm được hỗ trợ">
              <button type="button" aria-pressed={nhom === 'lanh-dao'} onClick={() => setNhom('lanh-dao')}>
                Lãnh đạo <span>{lanhDao.length}</span>
              </button>
              <button type="button" aria-pressed={nhom === 'truong-phong'} onClick={() => setNhom('truong-phong')}>
                Trưởng phòng <span>{truongPhong.length}</span>
              </button>
            </div>
            <TimKiem tuKhoa={tuKhoa} onDoi={setTuKhoa} nhan="Tìm người hoặc phòng ban" />
          </div>
          {danhSach.length === 0 ? (
            <p className="adm-empty">{tuTim ? 'Không có kết quả phù hợp.' : 'Chưa có tài khoản phù hợp.'}</p>
          ) : (
            <ul className="adm-assign-list">
              {danhSach.map((muc) => {
                const id = muc.nguoiDungId
                const dangPhanCong = phanCongTheoId.get(id)
                const biDungChung = (chuSoHuuThuKy.get(dangPhanCong?.thuKyId)?.length ?? 0) > 1
                const coTheChon = thuKy.filter((item) => item.nguoiDungId !== id
                  && !(chuSoHuuThuKy.get(item.nguoiDungId) ?? []).some((nguoiId) => nguoiId !== id))
                const thuKyDangChon = coTheChon.some((item) => item.nguoiDungId === dangPhanCong?.thuKyId)
                  ? dangPhanCong.thuKyId : ''
                const phong = muc.tenPhongBan || phongTheoId.get(muc.phongBanId)?.tenPhongBan
                return (
                  <li key={id}>
                    <div className="adm-assignee">
                      <span className="adm-mini-avatar" aria-hidden="true">{muc.hoTen?.trim().split(/\s+/).at(-1)?.[0]?.toLocaleUpperCase('vi')}</span>
                      <div><strong>{muc.hoTen}</strong>{phong && <small>{phong}</small>}
                        {biDungChung && <small className="adm-warning">Thư ký này đang được gán cho nhiều người.</small>}
                      </div>
                    </div>
                    <div className="adm-current">
                      <TrangThai ten={dangPhanCong ? 'Đã phân công' : 'Chưa phân công'} mau={dangPhanCong ? 'xanh' : 'xam'} />
                      {dangPhanCong && <small>{dangPhanCong.tenThuKy}</small>}
                    </div>
                    <form key={dangPhanCong?.thuKyId ?? 'chua-phan-cong'} onSubmit={(event) => luu(event, id)} className="adm-assign-form">
                      <select className="adm-input" aria-label={`Thư ký cho ${muc.hoTen}`} name="thuKyId" defaultValue={thuKyDangChon}
                        required disabled={coTheChon.length === 0 || dangLuuId !== null}>
                        <option value="">{coTheChon.length ? 'Chọn thư ký' : 'Hết thư ký khả dụng'}</option>
                        {coTheChon.map((item) => <option key={item.nguoiDungId} value={item.nguoiDungId}>{item.hoTen}</option>)}
                      </select>
                      <button type="submit" className="adm-btn adm-primary" disabled={coTheChon.length === 0 || dangLuuId !== null}>
                        {dangLuuId === id ? 'Đang lưu…' : 'Lưu phân công'}
                      </button>
                      {dangPhanCong && <button type="submit" value="huy" formNoValidate disabled={dangLuuId !== null}
                        className="adm-btn adm-secondary adm-danger-text">Bỏ phân công</button>}
                    </form>
                  </li>
                )
              })}
            </ul>
          )}
        </section>
      )}
    </section>
  )
}
