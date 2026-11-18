import { useEffect, useState } from 'react'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import { LOP_KHOI, LOP_TRUONG, LOP_NUT_CHINH } from '../../components/lopBieuMau.js'
import { huyPhanCongThuKy, layDuLieuPhanCongThuKy, luuPhanCongThuKy } from '../../api/cuocHopApi.js'

export default function PhanCongThuKy({ token }) {
  const phienDongBo = useDongBoDuLieu()
  const [duLieu, setDuLieu] = useState(null)
  const [loi, setLoi] = useState('')
  const [thongBao, setThongBao] = useState('')
  const [dangLuuId, setDangLuuId] = useState(null)

  useEffect(() => {
    const controller = new AbortController()
    layDuLieuPhanCongThuKy(token, controller.signal)
      .then((ketQua) => { if (!controller.signal.aborted) setDuLieu(ketQua) })
      .catch((error) => { if (!controller.signal.aborted) setLoi(error.message) })
    return () => controller.abort()
  }, [token, phienDongBo])

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

  return (
    <section className="space-y-5">
      <div>
        <h1 className="text-2xl font-bold">Phân công thư ký</h1>
        <p className="mt-1 text-sm text-slate-600">Mỗi lãnh đạo hoặc trưởng phòng có một thư ký mặc định riêng.</p>
      </div>
      {loi && <p role="alert" className="rounded-lg bg-red-50 p-3 text-sm text-red-800">{loi}</p>}
      {thongBao && <p role="status" className="rounded-lg bg-emerald-50 p-3 text-sm text-emerald-800">{thongBao}</p>}
      {!duLieu && !loi && <p role="status" className="text-sm text-slate-600">Đang tải dữ liệu phân công…</p>}
      {duLieu && [
        { ten: 'Thư ký lãnh đạo', danhSach: lanhDao },
        { ten: 'Thư ký trưởng phòng', danhSach: truongPhong },
      ].map(({ ten, danhSach }) => (
        <section key={ten} className={LOP_KHOI}>
          <h2 className="text-lg font-bold">{ten}</h2>
          {danhSach.length === 0 && <p className="text-sm text-slate-600">Chưa có tài khoản phù hợp.</p>}
          <ul className="divide-y divide-slate-200">
            {danhSach.map((muc) => {
              const id = muc.nguoiDungId
              const dangPhanCong = duLieu.phanCong.find((item) => item.nguoiDuocHoTroId === id)
              const biDungChung = (chuSoHuuThuKy.get(dangPhanCong?.thuKyId)?.length ?? 0) > 1
              const coTheChon = thuKy.filter((item) => item.nguoiDungId !== id
                && !(chuSoHuuThuKy.get(item.nguoiDungId) ?? []).some((nguoiId) => nguoiId !== id))
              const thuKyDangChon = coTheChon.some((item) => item.nguoiDungId === dangPhanCong?.thuKyId)
                ? dangPhanCong.thuKyId : ''
              return (
                <li key={id} className="grid gap-3 py-3 sm:grid-cols-[minmax(0,1fr)_minmax(0,1.25fr)] sm:items-center">
                  <div>
                    <p className="font-semibold">{muc.hoTen}</p>
                    <p className="text-sm text-slate-600">{muc.tenPhongBan || phongTheoId.get(muc.phongBanId)?.tenPhongBan}</p>
                    <p className="mt-1 text-xs text-slate-600">{dangPhanCong ? `Đang hỗ trợ · ${dangPhanCong.tenThuKy}` : 'Chưa phân công'}</p>
                    {biDungChung && <p className="mt-1 text-xs font-medium text-amber-800">Thư ký này đang được gán cho nhiều người.</p>}
                  </div>
                  <form key={dangPhanCong?.thuKyId ?? 'chua-phan-cong'} onSubmit={(event) => luu(event, id)} className="flex flex-wrap items-end gap-2">
                    <label className="min-w-32 flex-1 text-sm font-semibold">
                      <span className="sr-only">Thư ký cho {muc.hoTen}</span>
                      <select className={LOP_TRUONG} name="thuKyId" defaultValue={thuKyDangChon} required disabled={coTheChon.length === 0 || dangLuuId !== null}>
                        <option value="">{coTheChon.length ? 'Chọn thư ký' : 'Hết thư ký khả dụng'}</option>
                        {coTheChon.map((item) => <option key={item.nguoiDungId} value={item.nguoiDungId}>{item.hoTen}</option>)}
                      </select>
                    </label>
                    <button type="submit" className={LOP_NUT_CHINH} disabled={coTheChon.length === 0 || dangLuuId !== null}>{dangLuuId === id ? 'Đang lưu…' : 'Lưu phân công'}</button>
                    {dangPhanCong && (
                      <button type="submit" value="huy" formNoValidate disabled={dangLuuId !== null} className="rounded-lg border border-slate-300 px-3 py-2 text-sm font-semibold disabled:opacity-50">Bỏ phân công</button>
                    )}
                  </form>
                </li>
              )
            })}
          </ul>
        </section>
      ))}
    </section>
  )
}
