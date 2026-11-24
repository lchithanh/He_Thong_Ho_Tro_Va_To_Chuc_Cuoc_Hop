import { useEffect, useState } from 'react'
import Award from 'lucide-react/dist/esm/icons/award.mjs'
import { HopThoaiHoSo, LocHoSo, NhanHoSo, NutTaoHoSo, TieuDeHoSo } from '../../components/GiaoDienPhongHop.jsx'
import { khopTuKhoa, LOP_CHU_PHU, LOP_NOI, LOP_NUT_CHINH, LOP_NUT_PHU, LOP_THE, LOP_TRUONG } from '../../components/giaoDienPhongHop.js'
import { goiBauChon } from '../../api/nghiepVuCuocHopApi.js'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'

const NUT = LOP_NUT_CHINH
const MAU_TRANG_THAI = { DANG_MO: 'luc', DA_CHOT: 'xanh', CHO_PHAN_DINH: 'vang', DA_HUY: 'do' }
const MAU_KET_QUA = { DUOC_CHON: 'luc', DONG_PHIEU: 'vang' }
const BO_LOC = [['', 'Tất cả'], ['DANG_MO', 'Đang nhận phiếu'], ['NHAP', 'Chưa mở'], ['da-chot', 'Đã kết thúc']]
const KET_QUA = { DUOC_CHON: 'Được chọn', DONG_PHIEU: 'Đồng phiếu', LOAI: 'Dừng ở vòng này' }
const TRANG_THAI = { NHAP: 'Chưa mở', DANG_MO: 'Đang nhận phiếu', DA_CHOT: 'Đã chốt', CHO_PHAN_DINH: 'Chờ bầu phụ', DA_HUY: 'Đã đóng vì chưa có phiếu' }

function DanhSachChon({ danhSach, gioiHan, daChon, setDaChon }) {
  return (
    <ul className="grid max-h-64 gap-1 overflow-auto rounded-lg border border-slate-200 phong-toi:border-phong-vien p-2 sm:grid-cols-2">
      {danhSach.map(({ nguoiDungId, hoTen }) => (
        <li key={nguoiDungId}>
          <label className="flex min-h-10 cursor-pointer items-start gap-2 rounded-lg border border-slate-200 px-2 py-2 has-checked:border-blue-500 has-checked:bg-blue-50 phong-toi:border-phong-vien phong-toi:has-checked:border-phong-focus phong-toi:has-checked:bg-phong-nhan-xanh">
            <input type="checkbox" checked={daChon.includes(nguoiDungId)}
              className="mt-0.5 shrink-0 accent-blue-700 phong-toi:accent-phong-focus"
              disabled={daChon.length === gioiHan && !daChon.includes(nguoiDungId)}
              onChange={(event) => setDaChon(event.target.checked
                ? [...daChon, nguoiDungId] : daChon.filter((id) => id !== nguoiDungId))} />
            <span className="min-w-0 wrap-anywhere">{hoTen}</span>
          </label>
        </li>
      ))}
    </ul>
  )
}

function VongBau({ muc, thanhVien, laChuTri, lanBauPhu, dangGui, gui, chiXem }) {
  const [daChon, setDaChon] = useState([])
  const [cuTriIds, setCuTriIds] = useState([])
  const cuTriConCoMat = cuTriIds.filter((id) => thanhVien.some((nguoi) => nguoi.nguoiDungId === id))
  const { vong, ungVien, duocBoPhieu, daBoPhieu } = muc
  const { vongBauId, soVong, soNguoiDuocChon, soLuaChonMoiPhieu, trangThai } = vong
  const coTheBoPhieu = !chiXem && trangThai === 'DANG_MO' && duocBoPhieu && !daBoPhieu
  const hienKetQua = ['DA_CHOT', 'CHO_PHAN_DINH'].includes(trangThai)
  return (
    <section className={LOP_NOI + ' space-y-3'}>
      <div className="flex flex-wrap items-center justify-between gap-2">
        <h4 className="font-semibold">{lanBauPhu ? `Bầu phụ vòng ${soVong} · Lượt ${lanBauPhu}` : `Vòng ${soVong}`}</h4>
        <NhanHoSo mau={MAU_TRANG_THAI[trangThai]}>{TRANG_THAI[trangThai]}</NhanHoSo>
      </div>
      <p className={LOP_CHU_PHU}>{soNguoiDuocChon} suất · {soLuaChonMoiPhieu} lựa chọn mỗi phiếu</p>
      {hienKetQua && (
        <ul className="divide-y divide-slate-200 phong-toi:divide-phong-vien">
          {ungVien.map((u) => <li className="flex flex-wrap justify-between gap-2 py-1.5" key={u.nguoiDungId}>
            <span className="break-words font-medium">{u.hoTen}</span>{' '}
            <span className="flex flex-wrap items-center gap-2">
              <span className={LOP_CHU_PHU + ' tabular-nums'}>{u.soLuotChon} lượt chọn</span>
              {u.ketQua && <NhanHoSo mau={MAU_KET_QUA[u.ketQua]}>{KET_QUA[u.ketQua]}</NhanHoSo>}
            </span>
          </li>)}
        </ul>
      )}
      {coTheBoPhieu && (
        <form className="space-y-2" onSubmit={(event) => {
          event.preventDefault()
          gui(`/vong/${vongBauId}/phieu`, { ungVienIds: daChon })
        }}>
          <fieldset disabled={dangGui} className="space-y-2">
            <legend>Chọn đúng {soLuaChonMoiPhieu} ứng viên ({daChon.length}/{soLuaChonMoiPhieu})</legend>
            <DanhSachChon danhSach={ungVien} gioiHan={soLuaChonMoiPhieu} daChon={daChon} setDaChon={setDaChon} />
            <button className={NUT} disabled={daChon.length !== soLuaChonMoiPhieu}>Gửi phiếu</button>
          </fieldset>
        </form>
      )}
      {daBoPhieu && trangThai === 'DANG_MO' && <p role="status">Đã gửi phiếu.</p>}
      {!chiXem && laChuTri && trangThai === 'NHAP' && (
        <fieldset className="space-y-2" disabled={dangGui}>
          <legend>Người được bỏ phiếu ({cuTriConCoMat.length})</legend>
          {thanhVien.length > 0 ? (
            <>
              <button type="button" className="underline"
                onClick={() => setCuTriIds(thanhVien.map((nguoi) => nguoi.nguoiDungId))}>Chọn tất cả người có mặt</button>
              <DanhSachChon danhSach={thanhVien} daChon={cuTriConCoMat} setDaChon={setCuTriIds} />
            </>
          ) : <p>Chưa có thành viên được điểm danh có mặt.</p>}
          <button type="button" className={NUT} disabled={cuTriConCoMat.length === 0}
            onClick={() => gui(`/vong/${vongBauId}/mo`, { cuTriIds: cuTriConCoMat })}>Mở vòng bầu</button>
        </fieldset>
      )}
      {!chiXem && laChuTri && trangThai === 'DANG_MO' && <button type="button" className={LOP_NUT_PHU}
        disabled={dangGui} onClick={() => gui(`/vong/${vongBauId}/dong`)}>Đóng vòng và kiểm phiếu</button>}
    </section>
  )
}

export default function BauChonCuocHop({ cuocHopId, token, chiXem = false, hienTieuDe = true }) {
  const phien = useDongBoDuLieu()
  const [duLieu, setDuLieu] = useState(null)
  const [lanTai, setLanTai] = useState(0)
  const [ungVienIds, setUngVienIds] = useState([])
  const [cacVong, setCacVong] = useState([{}])
  const [loi, setLoi] = useState('')
  const [dangGui, setDangGui] = useState(false)
  const [moForm, setMoForm] = useState(false)
  const [tuKhoa, setTuKhoa] = useState('')
  const [boLoc, setBoLoc] = useState('')
  const hienThi = duLieu?.cuocBau.filter(({ cuocBau, cacVong }) => {
    const trangThai = cacVong.at(-1)?.vong.trangThai
    return khopTuKhoa(cuocBau.tieuDe, tuKhoa) && (!boLoc || (boLoc === 'da-chot'
      ? ['DA_CHOT', 'DA_HUY'].includes(trangThai) : trangThai === boLoc))
  })

  function dongForm() {
    setMoForm(false)
    setUngVienIds([])
    setCacVong([{}])
    setLoi('')
  }

  useEffect(() => {
    const controller = new AbortController()
    goiBauChon(cuocHopId, token, '', { signal: controller.signal })
      .then(setDuLieu)
      .catch((error) => { if (!controller.signal.aborted) setLoi(error.message) })
    return () => controller.abort()
  }, [cuocHopId, token, phien, lanTai])

  const ungVienConCoMat = ungVienIds.filter((id) => duLieu?.thanhVien.some((nguoi) => nguoi.nguoiDungId === id))

  async function gui(duongDan, duLieuGui) {
    if (dangGui) return false
    setDangGui(true)
    setLoi('')
    try {
      await goiBauChon(cuocHopId, token, duongDan, { method: 'POST', duLieu: duLieuGui })
      setLanTai((lan) => lan + 1)
      return true
    } catch (error) {
      setLoi(error.message)
      return false
    } finally {
      setDangGui(false)
    }
  }

  return (
    <div className="space-y-4 text-sm">
      <div className={LOP_THE + ' space-y-4'}>
        <TieuDeHoSo Icon={Award} cap={hienTieuDe ? 'h2' : 'h3'}
          thaoTac={duLieu?.laChuTri && !chiXem && <NutTaoHoSo disabled={dangGui} onClick={() => {
            setLoi('')
            setMoForm(true)
          }}>Tạo cuộc bầu chọn</NutTaoHoSo>}>
          Danh sách cuộc bầu chọn
        </TieuDeHoSo>
        {duLieu?.cuocBau.length > 0 && <LocHoSo tuKhoa={tuKhoa} onTuKhoa={setTuKhoa} nhanTim="Tìm cuộc bầu chọn"
          boLoc={boLoc} onBoLoc={setBoLoc} cacMuc={BO_LOC} />}
        {loi && !moForm && <p role="alert" className="text-red-800 phong-toi:text-phong-do">{loi}</p>}
        {!duLieu && !loi && <p role="status" className={LOP_CHU_PHU}>Đang tải cuộc bầu chọn…</p>}
        {hienThi?.length === 0 && <p role="status" className={LOP_CHU_PHU}>
          {duLieu.cuocBau.length ? 'Không tìm thấy cuộc bầu chọn phù hợp.' : 'Cuộc họp chưa có cuộc bầu chọn.'}
        </p>}
      </div>
      {duLieu?.laChuTri && !chiXem && moForm && (
        <HopThoaiHoSo Icon={Award} tieuDe="Tạo cuộc bầu chọn" dangLuu={dangGui} onDong={dongForm}>
          <form className="space-y-3 text-sm" onSubmit={async (event) => {
            event.preventDefault()
            const form = event.currentTarget
            if (await gui('', { tieuDe: new FormData(form).get('tieuDe'), ungVienIds: ungVienConCoMat, cacVong })) {
              setBoLoc('')
              setTuKhoa('')
              dongForm()
            }
          }}>
            <fieldset className="space-y-2" disabled={dangGui}>
              <label className="block">Tiêu đề
                <input name="tieuDe" required maxLength={255} className={LOP_TRUONG + ' mt-1'} />
              </label>
              <fieldset className="space-y-2">
                <legend>Ứng viên được bầu ({ungVienConCoMat.length}/{duLieu.thanhVien.length})</legend>
                {duLieu.thanhVien.length > 0 ? (
                  <>
                    <button type="button" className="underline"
                      onClick={() => setUngVienIds(duLieu.thanhVien.map((nguoi) => nguoi.nguoiDungId))}>Chọn tất cả người có mặt</button>
                    <DanhSachChon danhSach={duLieu.thanhVien} daChon={ungVienConCoMat} setDaChon={setUngVienIds} />
                  </>
                ) : <p>Chưa có thành viên được điểm danh có mặt.</p>}
              </fieldset>
              <fieldset className="space-y-2">
                <legend>Cấu hình vòng bầu</legend>
                {cacVong.map((vong, i) => {
                  const soCoTheChon = i === 0 ? ungVienConCoMat.length : cacVong[i - 1].soNguoiDuocChon
                  return (
                    <div key={i} className={LOP_NOI + ' grid gap-2 sm:grid-cols-2'}>
                      <p className="font-semibold sm:col-span-2">Vòng {i + 1}</p>
                      {[
                        ['soNguoiDuocChon', 'Số người trúng vòng', i === 0 ? soCoTheChon : soCoTheChon - 1],
                        ['soLuaChonMoiPhieu', 'Số người mỗi cử tri chọn', soCoTheChon],
                      ].map(([khoa, ten, toiDa]) => (
                        <label key={khoa} className="grid gap-1">
                          {ten}
                          <input type="number" required min={1} max={Math.max(toiDa || 0, 1)}
                            className={LOP_TRUONG} value={vong[khoa] ?? ''}
                            onChange={(event) => {
                              const giaTri = event.target.value === '' ? null : Number(event.target.value)
                              setCacVong((ds) => ds.map((muc, idx) => idx === i ? { ...muc, [khoa]: giaTri } : muc))
                            }} />
                        </label>
                      ))}
                    </div>
                  )
                })}
                <button type="button" className="mr-3 underline" disabled={(cacVong.at(-1)?.soNguoiDuocChon ?? 0) <= 1}
                  onClick={() => setCacVong((ds) => [...ds, {}])}>Thêm vòng</button>
                {cacVong.length > 1 && <button type="button" className="underline"
                  onClick={() => setCacVong((ds) => ds.slice(0, -1))}>Bỏ vòng cuối</button>}
              </fieldset>
              {loi && <p role="alert" className="text-rose-800 phong-toi:text-phong-do">{loi}</p>}
              <div className="flex flex-wrap justify-end gap-2 pt-2">
                <button type="button" className={LOP_NUT_PHU} onClick={dongForm}>Hủy</button>
                <button className={NUT} disabled={ungVienConCoMat.length === 0}>{dangGui ? 'Đang tạo…' : 'Tạo vòng 1'}</button>
              </div>
            </fieldset>
          </form>
        </HopThoaiHoSo>
      )}
      {hienThi?.map(({ cuocBau, cacVong }) => (
        <article className={LOP_THE + ' space-y-3'} key={cuocBau.cuocBauId}>
          <TieuDeHoSo Icon={Award} cap="h4">{cuocBau.tieuDe}</TieuDeHoSo>
          {cacVong.map((muc, i) => <VongBau key={muc.vong.vongBauId} muc={muc}
            thanhVien={duLieu.thanhVien} laChuTri={duLieu.laChuTri} dangGui={dangGui} gui={gui} chiXem={chiXem}
            lanBauPhu={cacVong.slice(0, i).filter(({ vong }) => vong.soVong === muc.vong.soVong).length} />)}
        </article>
      ))}
    </div>
  )
}
