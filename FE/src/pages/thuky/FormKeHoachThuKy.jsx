import { useState } from 'react'
import { capNhatKeHoach } from '../../api/cuocHopApi.js'

const LOP_TRUONG = 'mt-1 w-full rounded-lg border border-slate-300 px-3 py-2'

export default function FormKeHoachThuKy({
  chiTiet,
  token,
  onDaLuu,
  luaChonChuTri,
  luaChonThuKy,
  laBanCapNhat = false,
}) {
  const [dangLuu, setDangLuu] = useState(false)
  const [loi, setLoi] = useState('')
  const [thongBao, setThongBao] = useState('')
  async function xuLyLuu(event) {
    event.preventDefault()
    if (dangLuu) return

    const form = new FormData(event.currentTarget)
    const tieuDe = form.get('tieuDe').trim()

    setLoi('')
    setThongBao('')

    if (!luaChonChuTri?.length) return

    if (!tieuDe) {
      setLoi('Vui lòng nhập tiêu đề cuộc họp.')
      return
    }

    const duLieu = {
      tieuDe,
      mucTieu: form.get('mucTieu').trim(),
      noiDungDuKien: form.get('noiDungDuKien').trim(),
      loaiHinh: form.get('loaiHinh'),
      thuKyCuocHopId: Number(form.get('thuKyCuocHopId')) || null,
      nguoiChuTriId: Number(form.get('nguoiChuTriId')),
      coKhachMoiBenNgoai: form.get('coKhachMoiBenNgoai') === 'true',
    }

    setDangLuu(true)

    try {
      const ketQua = await capNhatKeHoach(chiTiet.cuocHopId, duLieu, token)
      onDaLuu(duLieu, ketQua)
      setThongBao(ketQua?.message || 'Đã lưu kế hoạch.')
    } catch (loiKhiLuu) {
      setLoi(
        loiKhiLuu.status === 403
          ? 'Tài khoản này không có quyền cập nhật kế hoạch.'
          : loiKhiLuu.message,
      )
    } finally {
      setDangLuu(false)
    }
  }

  return (
    <form onSubmit={xuLyLuu} className="space-y-4 border-t border-slate-200 pt-5">
      <h3 className="font-semibold">{laBanCapNhat ? 'Cập nhật kế hoạch đã duyệt' : 'Hoàn thiện kế hoạch'}</h3>

      <label className="block text-sm font-medium">
        Tiêu đề
        <input name="tieuDe" defaultValue={chiTiet.tieuDe} required className={LOP_TRUONG} />
      </label>

      <label className="block text-sm font-medium">
        Mục tiêu
        <textarea name="mucTieu" defaultValue={chiTiet.mucTieu ?? ''} rows={2} className={LOP_TRUONG} />
      </label>

      <label className="block text-sm font-medium">
        Nội dung dự kiến
        <textarea name="noiDungDuKien" defaultValue={chiTiet.noiDungDuKien ?? ''} rows={3} className={LOP_TRUONG} />
      </label>

      <div className="grid gap-4 sm:grid-cols-2">
        <label className="block text-sm font-medium">
          Hình thức
          <select name="loaiHinh" defaultValue={chiTiet.loaiHinh ?? ''} required className={LOP_TRUONG}>
            <option value="">Chọn hình thức</option>
            <option value="TRUC_TIEP">Trực tiếp</option>
            <option value="TRUC_TUYEN">Trực tuyến</option>
          </select>
        </label>

        <label className="block text-sm font-medium">
          Người chủ trì
          <select key={luaChonChuTri ? 'san-sang' : 'dang-tai'} name="nguoiChuTriId"
            defaultValue={chiTiet.nguoiChuTriId ?? ''} disabled={!luaChonChuTri?.length}
            required className={LOP_TRUONG}>
            <option value="">{!luaChonChuTri ? 'Đang tải danh sách…' : luaChonChuTri.length ? 'Chọn người chủ trì' : 'Chưa có người đủ điều kiện'}</option>
            {luaChonChuTri?.map((nguoi) => (
              <option key={nguoi.nguoiDungId} value={nguoi.nguoiDungId}>
                {nguoi.hoTen}{nguoi.tenPhongBan ? ` · ${nguoi.tenPhongBan}` : ''}
              </option>
            ))}
          </select>
        </label>
      </div>

      <label className="block text-sm font-medium">
        Thư ký cuộc họp
        <select key={luaChonThuKy ? 'da-tai' : 'dang-tai'} name="thuKyCuocHopId"
          defaultValue={chiTiet.thuKyCuocHopId ?? ''} disabled={!luaChonThuKy} className={LOP_TRUONG}>
          <option value="">Thư ký lập hồ sơ (mặc định)</option>
          {luaChonThuKy?.map((nguoi) => (
            <option key={nguoi.nguoiDungId} value={nguoi.nguoiDungId}>{nguoi.hoTen}{nguoi.tenPhongBan ? ` · ${nguoi.tenPhongBan}` : ''}</option>
          ))}
        </select>
      </label>
      <label className="block text-sm font-medium">
        Có khách mời bên ngoài
        <select
          name="coKhachMoiBenNgoai"
          defaultValue={chiTiet.coKhachMoiBenNgoai?.toString() ?? ''}
          required
          className={LOP_TRUONG}
        >
          <option value="">Chọn có hoặc không</option>
          <option value="true">Có</option>
          <option value="false">Không</option>
        </select>
      </label>

      {loi && <p role="alert" className="text-sm text-red-800">{loi}</p>}
      {thongBao && <p role="status" className="text-sm text-emerald-800">{thongBao}</p>}

      <button
        type="submit"
        disabled={dangLuu || !luaChonChuTri?.length}
        className="rounded-lg bg-emerald-800 px-4 py-2 text-sm font-semibold text-white disabled:opacity-60"
      >
        {dangLuu ? 'Đang lưu…' : laBanCapNhat ? 'Lưu bản cập nhật' : 'Lưu kế hoạch'}
      </button>
    </form>
  )
}



