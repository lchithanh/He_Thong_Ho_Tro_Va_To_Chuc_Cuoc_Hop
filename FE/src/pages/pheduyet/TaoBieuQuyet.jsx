import { useEffect, useState } from 'react'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import { taoBieuQuyet } from '../../api/bieuQuyetApi.js'
import { layDanhSachDeXuat } from '../../api/cuocHopApi.js'
import { layThanhVienCuocHop } from '../../api/thanhVienCuocHopApi.js'
import DanhSachBieuQuyetChuTri from './DanhSachBieuQuyetChuTri.jsx'


const LOP_TRUONG = 'mt-1 w-full rounded-lg border border-slate-300 px-3 py-2'

export default function TaoBieuQuyet({ nguoiDung, cuocHopId: cuocHopIdDuocChon }) {
  const phienDongBo = useDongBoDuLieu()
  const [cuocHop, setCuocHop] = useState(null)
  const [loiTai, setLoiTai] = useState('')
  const [loiLuu, setLoiLuu] = useState('')
  const [dangLuu, setDangLuu] = useState(false)
  const [ketQua, setKetQua] = useState(null)
  const [thanhVien, setThanhVien] = useState(null)
  const [loiThanhVien, setLoiThanhVien] = useState('')
  const [nguoiDuocBieuQuyetIds, setNguoiDuocBieuQuyetIds] = useState([])

  const { token, nguoiDungId } = nguoiDung

  const [cuocHopDangChon, setCuocHopDangChon] = useState(
    cuocHopIdDuocChon ? String(cuocHopIdDuocChon) : '',
  )
  const [lanTao, setLanTao] = useState(0)

  const cuocHopHienTai = cuocHop?.find(
    (muc) => String(muc.cuocHopId) === cuocHopDangChon,
  )
  useEffect(() => {
    const controller = new AbortController()

    layDanhSachDeXuat(token, controller.signal)
      .then((duLieu) => {
        if (controller.signal.aborted) return

        setCuocHop(duLieu.filter((muc) =>
          muc.nguoiChuTriId === nguoiDungId
          && ['DA_DUYET', 'DANG_DIEN_RA'].includes(muc.trangThai)
          && (!cuocHopIdDuocChon || muc.cuocHopId === Number(cuocHopIdDuocChon)),
        ))
      })
      .catch((loi) => {
        if (!controller.signal.aborted) setLoiTai(loi.message)
      })

    return () => controller.abort()
  }, [token, nguoiDungId, cuocHopIdDuocChon, phienDongBo])

  useEffect(() => {
    if (!cuocHopDangChon) {
      setThanhVien(null)
      setLoiThanhVien('')
      setNguoiDuocBieuQuyetIds([])
      return undefined
    }

    const controller = new AbortController()
    setThanhVien(null)
    setLoiThanhVien('')
    setNguoiDuocBieuQuyetIds([])

    layThanhVienCuocHop(Number(cuocHopDangChon), token, controller.signal)
      .then((duLieu) => {
        if (!controller.signal.aborted) {
          setThanhVien(duLieu)
          setLoiThanhVien('')
        }
      })
      .catch((loi) => {
        if (!controller.signal.aborted) setLoiThanhVien(loi.message)
      })

    return () => controller.abort()
  }, [cuocHopDangChon, token])

  async function xuLyTao(event) {
    event.preventDefault()
    if (dangLuu) return

    const form = event.currentTarget
    const duLieuForm = new FormData(form)
    const cuocHopId = Number(duLieuForm.get('cuocHopId'))
    const tieuDe = duLieuForm.get('tieuDe').trim()
    const batDau = duLieuForm.get('thoiGianBatDau')
    const ketThuc = duLieuForm.get('thoiGianKetThuc')
    setLoiLuu('')
    setKetQua(null)

    if (!tieuDe || ketThuc <= batDau) {
      setLoiLuu('Nhập tiêu đề và chọn giờ kết thúc sau giờ bắt đầu.')
      return
    }
    if (nguoiDuocBieuQuyetIds.length === 0) {
      setLoiLuu('Chọn ít nhất một thành viên có quyền tham gia biểu quyết.')
      return
    }

    setDangLuu(true)

    try {
      const phanHoi = await taoBieuQuyet(cuocHopId, {
        tieuDe,
        noiDung: duLieuForm.get('noiDung').trim() || null,
        cheDoBieuQuyet: duLieuForm.get('cheDoBieuQuyet'),
        thoiGianBatDau: batDau,
        thoiGianKetThuc: ketThuc,
        nguoiDuocBieuQuyetIds,
      }, token)

      setKetQua({ cuocHopId, bieuQuyetId: phanHoi.bieuQuyetId })
      setNguoiDuocBieuQuyetIds([])
      setLanTao((hienTai) => hienTai + 1)
      form.reset()
    } catch (loi) {
      setLoiLuu(loi.message)
    } finally {
      setDangLuu(false)
    }
  }

  return (
    <section className="space-y-5">
      <div>
        <p className="text-xs font-bold uppercase tracking-[0.14em] text-emerald-800">Ý kiến cuộc họp</p>
        <h2 className="mt-1 text-xl font-bold text-slate-900">Tạo biểu quyết</h2>
        <p className="mt-1 text-sm text-slate-500">Tạo phiên biểu quyết và theo dõi tình trạng bỏ phiếu, kết quả.</p>
      </div>

      {loiTai && <p role="alert" className="text-sm text-red-800">{loiTai}</p>}
      {!loiTai && cuocHop === null && <p role="status">Đang tải cuộc họp…</p>}
      {cuocHop?.length === 0 && (
        <p className="text-sm text-slate-600">
          Chưa có cuộc họp đủ điều kiện trong danh sách của bạn.
        </p>
      )}

      {cuocHop?.length > 0 && (
        <form onSubmit={xuLyTao} className="space-y-4">
          <fieldset disabled={dangLuu} className="space-y-4 text-sm">
            {cuocHopIdDuocChon ? (
              <input type="hidden" name="cuocHopId" value={cuocHopIdDuocChon} />
            ) : (
              <label className="block font-medium">
                Cuộc họp
                <select
                  name="cuocHopId"
                  value={cuocHopDangChon}
                  onChange={(event) => setCuocHopDangChon(event.target.value)}
                  required
                  className={LOP_TRUONG}
                >
                  <option value="">Chọn cuộc họp</option>
                  {cuocHop.map((muc) => (
                    <option key={muc.cuocHopId} value={muc.cuocHopId}>
                      {muc.maCuocHop || `#${muc.cuocHopId}`} · {muc.tieuDe}
                    </option>
                  ))}
                </select>
              </label>
            )}

            <label className="block font-medium">
              Tiêu đề biểu quyết
              <input name="tieuDe" maxLength={255} required className={LOP_TRUONG} />
            </label>

            <label className="block font-medium">
              Nội dung cần biểu quyết
              <textarea name="noiDung" rows={3} className={LOP_TRUONG} />
            </label>

            <div className="grid gap-3 sm:grid-cols-2">
              <label className="block font-medium">
                Chế độ
                <select name="cheDoBieuQuyet" className={LOP_TRUONG}>
                  <option value="CONG_KHAI">Công khai</option>
                  <option value="AN_DANH">Ẩn danh</option>
                </select>
              </label>

              <label className="block font-medium">
                Thời gian bắt đầu
                <input name="thoiGianBatDau" type="datetime-local" required className={LOP_TRUONG} />
              </label>

              <label className="block font-medium">
                Thời gian kết thúc
                <input name="thoiGianKetThuc" type="datetime-local" required className={LOP_TRUONG} />
              </label>
            </div>

            {cuocHopHienTai && (
              <fieldset className="space-y-2 rounded-lg border border-slate-200 p-3">
                <legend className="px-1 font-semibold">Người có quyền bỏ phiếu</legend>
                <p className="text-xs text-slate-600">
                  Chọn thành viên được bỏ phiếu. Khi mở biểu quyết, chỉ người có mặt mới được tham gia.
                </p>
                {loiThanhVien && <p role="alert" className="text-sm text-red-800">{loiThanhVien}</p>}
                {thanhVien === null && !loiThanhVien && (
                  <p role="status" className="text-sm text-slate-600">Đang tải thành viên…</p>
                )}
                {thanhVien?.length === 0 && (
                  <p className="text-sm text-slate-600">Cuộc họp chưa có thành viên để chọn.</p>
                )}
                {thanhVien?.length > 0 && (
                  <>
                    <div className="flex flex-wrap gap-2">
                      <button
                        type="button"
                        disabled={dangLuu}
                        onClick={() => setNguoiDuocBieuQuyetIds(
                          thanhVien.map((nguoi) => nguoi.nguoiDungId),
                        )}
                        className="rounded border border-slate-300 px-2 py-1 text-xs font-medium"
                      >
                        Chọn tất cả
                      </button>
                      <button
                        type="button"
                        disabled={dangLuu}
                        onClick={() => setNguoiDuocBieuQuyetIds([])}
                        className="rounded border border-slate-300 px-2 py-1 text-xs font-medium"
                      >
                        Bỏ chọn
                      </button>
                      <span className="self-center text-xs text-slate-600">
                        Đã chọn {nguoiDuocBieuQuyetIds.length}/{thanhVien.length}
                      </span>
                    </div>
                    <ul className="max-h-64 space-y-2 overflow-y-auto">
                      {thanhVien.map((nguoi) => (
                        <li key={nguoi.nguoiDungId}>
                          <label className="flex cursor-pointer items-start gap-2 rounded-md p-2 hover:bg-slate-50">
                            <input
                              type="checkbox"
                              checked={nguoiDuocBieuQuyetIds.includes(nguoi.nguoiDungId)}
                              onChange={(event) => setNguoiDuocBieuQuyetIds((hienTai) =>
                                event.target.checked
                                  ? [...hienTai, nguoi.nguoiDungId]
                                  : hienTai.filter((id) => id !== nguoi.nguoiDungId),
                              )}
                              className="mt-1"
                            />
                            <span className="min-w-0 text-sm">
                              <span className="block font-medium">{nguoi.hoTen}</span>
                              <span className="block text-xs text-slate-600">
                                {nguoi.tenPhongBan || 'Chưa có phòng ban'} · {nguoi.chucVu || 'Chưa có chức vụ'}
                                {' · Vai trò: '}{nguoi.vaiTroTaiCuocHop || 'Thành viên'}
                              </span>
                              <span className="block text-xs text-slate-500">
                                {nguoi.trangThaiMoi} · Điểm danh: {nguoi.trangThaiThamDu}
                              </span>
                            </span>
                          </label>
                        </li>
                      ))}
                    </ul>
                  </>
                )}
              </fieldset>
            )}
          </fieldset>

          {loiLuu && <p role="alert" className="text-sm text-red-800">{loiLuu}</p>}

          <button
            type="submit"
            disabled={dangLuu || !cuocHopHienTai || thanhVien === null
              || Boolean(loiThanhVien) || nguoiDuocBieuQuyetIds.length === 0}
            className="rounded-lg bg-emerald-800 px-4 py-2 text-sm font-semibold text-white disabled:opacity-60"
          >
            {dangLuu ? 'Đang tạo…' : 'Tạo bản nháp biểu quyết'}
          </button>
        </form>
      )}

      {ketQua && (
        <p role="status" className="text-sm text-emerald-800">
          Đã tạo biểu quyết #{ketQua.bieuQuyetId} cho cuộc họp #{ketQua.cuocHopId}.
        </p>
      )}

      {cuocHopHienTai && (
        <DanhSachBieuQuyetChuTri
      key={cuocHopHienTai.cuocHopId}
      cuocHopId={cuocHopHienTai.cuocHopId}
      token={token}
      dangDienRa={cuocHopHienTai.trangThai === 'DANG_DIEN_RA'}
      lanTao={lanTao}
        />
      )}
    </section>
  )
}



