import Vote from 'lucide-react/dist/esm/icons/vote.mjs'
import { HopThoaiHoSo, NutTaoHoSo, TieuDeHoSo } from '../../components/GiaoDienPhongHop.jsx'
import { LOP_NUT_CHINH, LOP_NUT_PHU, LOP_THE } from '../../components/giaoDienPhongHop.js'
import { useEffect, useState } from 'react'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import { goiNghiQuyet } from '../../api/nghiepVuCuocHopApi.js'
import { capNhatBieuQuyet, layChiTietBieuQuyet, taoBieuQuyet } from '../../api/bieuQuyetApi.js'
import { layDanhSachDeXuat } from '../../api/cuocHopApi.js'
import { layThanhVienCuocHop } from '../../api/thanhVienCuocHopApi.js'
import { soPhutGiuaHaiMoc } from '../../utils/thoiGianCuocHop.js'
import DanhSachBieuQuyetChuTri from './DanhSachBieuQuyetChuTri.jsx'


const LOP_TRUONG = 'mt-1 w-full rounded-lg border border-slate-300 px-3 py-2 phong-toi:truong-phong-hop'

const CAC_MOC_PHUT = [5, 10, 15, 30]
const PHUONG_AN_TRONG = [{ id: 1, noiDung: '' }, { id: 2, noiDung: '' }]

export default function TaoBieuQuyet({ nguoiDung, cuocHopId: cuocHopIdDuocChon, nghiQuyetGoc }) {
  const phienDongBo = useDongBoDuLieu()
  const [cuocHop, setCuocHop] = useState(null)
  const [loiTai, setLoiTai] = useState('')
  const [moForm, setMoForm] = useState(false)
  const [mocPhut, setMocPhut] = useState('5')
  const [phutTuyChinh, setPhutTuyChinh] = useState('')
  const [banSua, setBanSua] = useState(null)
  const [phuongThuc, setPhuongThuc] = useState('TAN_THANH')
  const [phuongAnNhap, setPhuongAnNhap] = useState(PHUONG_AN_TRONG)
  const [loiLuu, setLoiLuu] = useState('')
  const [dangLuu, setDangLuu] = useState(false)
  const [ketQua, setKetQua] = useState(null)
  const [nghiQuyet, setNghiQuyet] = useState([])
  const [nghiQuyetId, setNghiQuyetId] = useState('')
  const [loiNghiQuyet, setLoiNghiQuyet] = useState('')
  const nghiQuyetDaChon = nghiQuyetGoc ?? (banSua?.nghiQuyetId ? banSua
    : nghiQuyet.find((muc) => String(muc.nghiQuyetId) === nghiQuyetId))
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
          Number(muc.nguoiChuTriId) === Number(nguoiDungId)
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

  useEffect(() => {
    if (!cuocHopDangChon) return undefined
    const controller = new AbortController()
    goiNghiQuyet(cuocHopDangChon, token, '', { signal: controller.signal })
      .then((duLieu) => {
        if (controller.signal.aborted) return
        setNghiQuyet(duLieu.filter((muc) => muc.trangThai === 'NHAP' && muc.ngayCongBo && muc.taiLieuId))
        setLoiNghiQuyet('')
      })
      .catch((error) => { if (!controller.signal.aborted) setLoiNghiQuyet(error.message) })
    return () => controller.abort()
  }, [cuocHopDangChon, token, phienDongBo, lanTao])

  async function xuLyTao(event) {
    event.preventDefault()
    if (dangLuu) return

    const form = event.currentTarget
    const duLieuForm = new FormData(form)
    const cuocHopId = Number(cuocHopDangChon)
    const tieuDe = (nghiQuyetDaChon?.tieuDe ?? duLieuForm.get('tieuDe') ?? '').trim()
    const thoiLuongPhut = Number(mocPhut === 'tuy-chinh' ? phutTuyChinh : mocPhut)
    const moNgay = event.nativeEvent.submitter?.value === 'mo'
    setLoiLuu('')
    setKetQua(null)

    if (!tieuDe) {
      setLoiLuu('Nhập tiêu đề biểu quyết.')
      return
    }
    if (!Number.isInteger(thoiLuongPhut) || thoiLuongPhut < 1 || thoiLuongPhut > 2147483647) {
      setLoiLuu('Nhập thời lượng theo số phút nguyên lớn hơn 0.')
      return
    }
    if (nguoiDuocBieuQuyetIds.length === 0) {
      setLoiLuu('Chọn ít nhất một thành viên có quyền tham gia biểu quyết.')
      return
    }

    const phuongAn = phuongThuc === 'TAN_THANH' ? undefined
      : phuongAnNhap.map(({ noiDung }) => noiDung.trim().normalize('NFC'))
    if (phuongAn && (phuongAn.length < 2 || phuongAn.length > 20
      || phuongAn.some((muc) => !muc || muc.length > 255)
      || new Set(phuongAn.map((muc) => muc.toLowerCase())).size !== phuongAn.length)) {
      setLoiLuu('Nhập từ 2 đến 20 phương án khác nhau, mỗi phương án tối đa 255 ký tự.')
      return
    }
    setDangLuu(true)

    try {
      const duLieu = {
        tieuDe,
        noiDung: duLieuForm.get('noiDung')?.trim() || null,
        cheDoBieuQuyet: phuongThuc === 'TAN_THANH' ? duLieuForm.get('cheDoBieuQuyet') : 'CONG_KHAI',
        phuongThuc,
        phuongAn,
        thoiLuongPhut,
        moNgay,
        nguoiDuocBieuQuyetIds,
        nghiQuyetId: nghiQuyetDaChon?.nghiQuyetId,
      }
      if (banSua) {
        await capNhatBieuQuyet(cuocHopId, banSua.bieuQuyetId, duLieu, token)
        setKetQua({ bieuQuyetId: banSua.bieuQuyetId, daCapNhat: true })
      } else {
        const phanHoi = await taoBieuQuyet(cuocHopId, duLieu, token)
        setKetQua({ bieuQuyetId: phanHoi.bieuQuyetId, moNgay })
      }
      dongForm()
      setLanTao((hienTai) => hienTai + 1)
    } catch (loi) {
      setLoiLuu(loi.message)
    } finally {
      setDangLuu(false)
    }
  }

  async function moBanSua(id) {
    if (dangLuu) return
    setDangLuu(true)
    setLoiLuu('')
    try {
      const chiTiet = await layChiTietBieuQuyet(Number(cuocHopDangChon), id, token)
      if (chiTiet.trangThai !== 'NHAP') throw new Error('Phiên biểu quyết đã rời trạng thái nháp.')
      const soPhut = chiTiet.thoiLuongPhut
        ?? Math.max(soPhutGiuaHaiMoc(chiTiet.thoiGianBatDau, chiTiet.thoiGianKetThuc) ?? 5, 1)
      setMocPhut(CAC_MOC_PHUT.includes(soPhut) ? String(soPhut) : 'tuy-chinh')
      setPhutTuyChinh(String(soPhut))
      setBanSua(chiTiet)
      setNghiQuyetId('')
      const laChonPhuongAn = ['CHON_MOT', 'CHON_NHIEU'].includes(chiTiet.phuongThuc)
      setPhuongThuc(laChonPhuongAn ? 'CHON_MOT' : 'TAN_THANH')
      setPhuongAnNhap(laChonPhuongAn
        ? chiTiet.luaChon.map((muc, index) => ({ id: index + 1, noiDung: muc.noiDung }))
        : PHUONG_AN_TRONG)
      setNguoiDuocBieuQuyetIds(chiTiet.nguoiDuocBieuQuyetIds ?? [])
      setMoForm(true)
    } catch (error) {
      setLoiLuu(error.message)
    } finally {
      setDangLuu(false)
    }
  }

  function dongForm() {
    setMocPhut('5')
    setPhutTuyChinh('')
    setMoForm(false)
    setBanSua(null)
    setNghiQuyetId('')
    setPhuongThuc('TAN_THANH')
    setPhuongAnNhap(PHUONG_AN_TRONG)
    setNguoiDuocBieuQuyetIds([])
    setLoiLuu('')
  }

  const coTheTao = !nghiQuyetGoc || (nghiQuyetGoc.ngayCongBo && nghiQuyetGoc.trangThai === 'NHAP')
  const hienForm = moForm && Boolean(coTheTao || banSua) && Boolean(cuocHop?.length)
  return (
    <div className="space-y-5">
      {coTheTao && (
        <div className={LOP_THE}>
          <TieuDeHoSo Icon={Vote} thaoTac={<NutTaoHoSo disabled={dangLuu || !cuocHop?.length} onClick={() => setMoForm(true)}>
            Tạo biểu quyết
          </NutTaoHoSo>}>Điều hành biểu quyết</TieuDeHoSo>
        </div>
      )}
      {!hienForm && loiLuu && <p role="alert" className="text-sm text-red-800 phong-toi:text-phong-do">{loiLuu}</p>}

      {loiTai && <p role="alert" className="text-sm text-red-800 phong-toi:text-phong-do">{loiTai}</p>}
      {!loiTai && cuocHop === null && <p role="status">Đang tải cuộc họp…</p>}
      {cuocHop?.length === 0 && (
        <p className="text-sm text-slate-600 phong-toi:text-phong-chu-phu">
          Chưa có cuộc họp đủ điều kiện trong danh sách của bạn.
        </p>
      )}

      {hienForm && (
        <HopThoaiHoSo Icon={Vote} tieuDe={banSua ? 'Cập nhật biểu quyết' : 'Tạo biểu quyết'}
          dangLuu={dangLuu} onDong={dongForm}>
          <form key={banSua?.bieuQuyetId ?? 'tao'} onSubmit={xuLyTao} className="space-y-4">
            <fieldset disabled={dangLuu} className="space-y-4 text-sm">
              {cuocHopIdDuocChon ? (
                <input type="hidden" name="cuocHopId" value={cuocHopIdDuocChon} />
              ) : (
                <label className="block font-medium">
                  Cuộc họp
                  <select
                    name="cuocHopId"
                    value={cuocHopDangChon}
                    disabled={Boolean(banSua)}
                    onChange={(event) => {
                      setNghiQuyetId('')
                      setNghiQuyet([])
                      setPhuongAnNhap(PHUONG_AN_TRONG)
                      setCuocHopDangChon(event.target.value)
                    }}
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

              {loiNghiQuyet && <p role="alert" className="text-red-800 phong-toi:text-phong-do">{loiNghiQuyet}</p>}
              {!banSua && !nghiQuyetGoc && nghiQuyet.length > 0 && (
                <label className="block font-medium">Nghị quyết đưa ra biểu quyết
                  <select value={nghiQuyetId} onChange={(event) => setNghiQuyetId(event.target.value)} className={LOP_TRUONG}>
                    <option value="">Biểu quyết nội dung khác</option>
                    {nghiQuyet.map((muc) => <option key={muc.nghiQuyetId} value={muc.nghiQuyetId}>
                      {muc.tieuDe} (cấp {muc.cap})
                    </option>)}
                  </select>
                </label>
              )}
              {nghiQuyetDaChon ? (
                <div className="space-y-2 rounded-lg bg-slate-50 phong-toi:bg-phong-mat-noi p-3">
                  <p className="font-semibold">{nghiQuyetDaChon.tieuDe}</p>
                  <p className="whitespace-pre-wrap break-words">{nghiQuyetDaChon.noiDung}</p>
                  <p>{phuongThuc === 'TAN_THANH'
                    ? 'Nghị quyết thông qua khi trên một nửa số người có quyền bỏ phiếu chọn Đồng ý.'
                    : 'Phương án dẫn đầu phải duy nhất và được hơn một nửa số người có quyền bỏ phiếu chọn.'}</p>
                </div>
              ) : (
                <>
                  <label className="block font-medium">Tiêu đề biểu quyết
                    <input name="tieuDe" defaultValue={banSua?.tieuDe} maxLength={255} required className={LOP_TRUONG} />
                  </label>
                  <label className="block font-medium">Nội dung cần biểu quyết
                    <textarea name="noiDung" defaultValue={banSua?.noiDung ?? ''} rows={3} className={LOP_TRUONG} />
                  </label>
                </>
              )}

              <label className="block font-medium">Phương thức biểu quyết
                <select name="phuongThuc" className={LOP_TRUONG} value={phuongThuc} onChange={(event) => setPhuongThuc(event.target.value)}>
                  <option value="TAN_THANH">Đồng ý / Không đồng ý / Phiếu trắng</option>
                  <option value="CHON_MOT">Chọn một phương án</option>
                </select>
              </label>
              {phuongThuc === 'CHON_MOT' && (
                <fieldset className="space-y-3 rounded-lg border border-slate-300 p-3 phong-toi:border-phong-vien">
                  <legend className="px-1 font-semibold">Phương án để bỏ phiếu</legend>
                  <p className="text-sm text-slate-600 phong-toi:text-phong-chu-phu">
                    Nhập ít nhất hai phương án. Mỗi người chỉ chọn một phương án.
                  </p>
                  {phuongAnNhap.map(({ id, noiDung }, index) => (
                    <div key={id} className="flex flex-wrap items-end gap-2">
                      <label className="block min-w-0 flex-1 basis-56 font-medium">
                        Phương án {index + 1}
                        <input name="phuongAn" className={LOP_TRUONG} required maxLength={255}
                          placeholder={`Cách làm ${String.fromCharCode(65 + index)}`} value={noiDung}
                          onChange={(event) => {
                            const giaTri = event.target.value
                            setPhuongAnNhap((cacMuc) => cacMuc.map((muc) => muc.id === id ? { ...muc, noiDung: giaTri } : muc))
                          }} />
                      </label>
                      <button type="button" className={LOP_NUT_PHU} disabled={phuongAnNhap.length <= 2}
                        aria-label={`Bỏ phương án ${index + 1}`}
                        onClick={() => setPhuongAnNhap((cacMuc) => cacMuc.filter((muc) => muc.id !== id))}>Bỏ phương án</button>
                    </div>
                  ))}
                  <button type="button" className={LOP_NUT_PHU} disabled={phuongAnNhap.length >= 20}
                    onClick={() => setPhuongAnNhap((cacMuc) => [...cacMuc, { id: cacMuc.at(-1).id + 1, noiDung: '' }])}>
                    Thêm phương án
                  </button>
                </fieldset>
              )}

              <div className="grid gap-3 sm:grid-cols-2">
                {phuongThuc === 'TAN_THANH' ? (
                  <label className="block font-medium">Chế độ
                    <select name="cheDoBieuQuyet" className={LOP_TRUONG} defaultValue={banSua?.cheDoBieuQuyet ?? 'CONG_KHAI'}>
                      <option value="CONG_KHAI">Công khai</option>
                      <option value="AN_DANH">Ẩn danh</option>
                    </select>
                  </label>
                ) : (
                  <div>
                    <p className="font-medium">Chế độ</p>
                    <p className="mt-2 text-slate-700 phong-toi:text-phong-chu-phu">Công khai</p>
                  </div>
                )}

                <div>
                  <label className="block font-medium">Thời lượng
                    <select name="mocPhut" value={mocPhut} className={LOP_TRUONG} onChange={(event) => setMocPhut(event.target.value)}>
                      {CAC_MOC_PHUT.map((phut) => <option key={phut} value={phut}>{phut} phút</option>)}
                      <option value="tuy-chinh">Tự nhập</option>
                    </select>
                  </label>
                  {mocPhut === 'tuy-chinh' && (
                    <label className="mt-2 block font-medium">Số phút
                      <input type="number" name="thoiLuongPhut" min="1" max="2147483647" step="1" required
                        className={LOP_TRUONG} value={phutTuyChinh} onChange={(event) => setPhutTuyChinh(event.target.value)} />
                    </label>
                  )}
                  <p className="mt-2 text-xs text-slate-600 phong-toi:text-phong-chu-phu">Thời lượng tính từ lúc mở biểu quyết.</p>
                </div>
              </div>

              {cuocHopHienTai && (
                <fieldset className="space-y-2 rounded-lg border border-slate-200 phong-toi:border-phong-vien p-3">
                  <legend className="px-1 font-semibold">Người có quyền bỏ phiếu</legend>
                  <p className="text-xs text-slate-600 phong-toi:text-phong-chu-phu">
                    Chọn thành viên được bỏ phiếu. Khi mở biểu quyết, chỉ người có mặt mới được tham gia.
                  </p>
                  {loiThanhVien && <p role="alert" className="text-sm text-red-800 phong-toi:text-phong-do">{loiThanhVien}</p>}
                  {thanhVien === null && !loiThanhVien && (
                    <p role="status" className="text-sm text-slate-600 phong-toi:text-phong-chu-phu">Đang tải thành viên…</p>
                  )}
                  {thanhVien?.length === 0 && (
                    <p className="text-sm text-slate-600 phong-toi:text-phong-chu-phu">Cuộc họp chưa có thành viên để chọn.</p>
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
                          className="rounded border border-slate-300 phong-toi:border-phong-vien px-2 py-1 text-xs font-medium"
                        >
                          Chọn tất cả
                        </button>
                        <button
                          type="button"
                          disabled={dangLuu}
                          onClick={() => setNguoiDuocBieuQuyetIds([])}
                          className="rounded border border-slate-300 phong-toi:border-phong-vien px-2 py-1 text-xs font-medium"
                        >
                          Bỏ chọn
                        </button>
                        <span className="self-center text-xs text-slate-600 phong-toi:text-phong-chu-phu">
                          Đã chọn {nguoiDuocBieuQuyetIds.length}/{thanhVien.length}
                        </span>
                      </div>
                      <ul className="max-h-64 space-y-2 overflow-y-auto">
                        {thanhVien.map((nguoi) => (
                          <li key={nguoi.nguoiDungId}>
                            <label className="flex cursor-pointer items-start gap-2 rounded-md p-2 hover:bg-slate-50 phong-toi:hover:bg-phong-mat-noi">
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
                                <span className="block text-xs text-slate-600 phong-toi:text-phong-chu-phu">
                                  {nguoi.tenPhongBan || 'Chưa có phòng ban'} · {nguoi.chucVu || 'Chưa có chức vụ'}
                                  {' · Vai trò: '}{nguoi.vaiTroTaiCuocHop || 'Thành viên'}
                                </span>
                                <span className="block text-xs text-slate-500 phong-toi:text-phong-chu-phu">
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

            {loiLuu && <p role="alert" className="text-sm text-red-800 phong-toi:text-phong-do">{loiLuu}</p>}
            <fieldset disabled={dangLuu || !cuocHopHienTai || thanhVien === null || Boolean(loiThanhVien) || nguoiDuocBieuQuyetIds.length === 0}
              className="flex flex-wrap gap-2">
              <button type="submit" value="nhap" className={LOP_NUT_PHU}>
                {dangLuu ? 'Đang lưu…' : banSua ? 'Lưu cập nhật' : 'Lưu nháp'}
              </button>
              {!banSua && cuocHopHienTai?.trangThai === 'DANG_DIEN_RA' && (
                <button type="submit" value="mo" className={LOP_NUT_CHINH}>
                  Tạo và mở
                </button>
              )}
            </fieldset>
            <button className={LOP_NUT_PHU} type="button" onClick={dongForm} disabled={dangLuu}>Hủy</button>
          </form>
        </HopThoaiHoSo>
      )}

      {ketQua && (
        <p role="status" className="text-sm text-emerald-800 phong-toi:text-phong-xanh">
          {ketQua.daCapNhat ? 'Đã cập nhật' : ketQua.moNgay ? 'Đã tạo và mở' : 'Đã tạo bản nháp'} biểu quyết #{ketQua.bieuQuyetId}.
        </p>
      )}

      {cuocHopHienTai && (
        <DanhSachBieuQuyetChuTri
        key={cuocHopHienTai.cuocHopId}
        cuocHopId={cuocHopHienTai.cuocHopId}
        token={token}
        dangDienRa={cuocHopHienTai.trangThai === 'DANG_DIEN_RA'}
        lanTao={lanTao}
        nghiQuyetId={nghiQuyetGoc?.nghiQuyetId}
        onSua={moBanSua}
        />
      )}
    </div>
  )
}
