import { useEffect, useState } from 'react'
import { goiBauChon } from '../../api/nghiepVuCuocHopApi.js'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'

const NUT = 'rounded-lg bg-emerald-800 px-3 py-2 font-semibold text-white disabled:opacity-50'
const KET_QUA = { DUOC_CHON: 'Được chọn', DONG_PHIEU: 'Đồng phiếu', LOAI: 'Dừng ở vòng này' }
const TRANG_THAI = { NHAP: 'Chưa mở', DANG_MO: 'Đang nhận phiếu', DA_CHOT: 'Đã chốt', CHO_PHAN_DINH: 'Chờ bầu phụ', DA_HUY: 'Đã đóng vì chưa có phiếu' }

function DanhSachChon({ danhSach, gioiHan, daChon, setDaChon }) {
  return (
    <ul className="grid max-h-64 gap-1 overflow-auto rounded-lg border border-slate-200 p-2 sm:grid-cols-2">
      {danhSach.map(({ nguoiDungId, hoTen }) => (
        <li key={nguoiDungId}>
          <label className="flex items-center gap-2 p-1">
            <input type="checkbox" checked={daChon.includes(nguoiDungId)}
              disabled={daChon.length === gioiHan && !daChon.includes(nguoiDungId)}
              onChange={(event) => setDaChon(event.target.checked
                ? [...daChon, nguoiDungId] : daChon.filter((id) => id !== nguoiDungId))} />
            {hoTen}
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
    <section className="space-y-2 rounded-lg bg-slate-50 p-3">
      <h4 className="font-semibold">
        {lanBauPhu ? `Bầu phụ vòng ${soVong} · Lượt ${lanBauPhu}` : `Vòng ${soVong}`} · {TRANG_THAI[trangThai]}
      </h4>
      <p className="text-slate-600">{soNguoiDuocChon} suất · {soLuaChonMoiPhieu} lựa chọn mỗi phiếu</p>
      {hienKetQua && (
        <ul className="divide-y divide-slate-200">
          {ungVien.map((u) => <li className="flex flex-wrap justify-between gap-2 py-1.5" key={u.nguoiDungId}>
            <span className="break-words font-medium">{u.hoTen}</span>{' '}
            <span className="text-slate-600"><span aria-hidden="true">· </span>{u.soLuotChon} lượt chọn · {KET_QUA[u.ketQua]}</span>
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
      {!chiXem && laChuTri && trangThai === 'DANG_MO' && <button type="button" className="rounded-lg border px-3 py-2"
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
      {hienTieuDe && <h2 className="text-lg font-bold">Bầu chọn</h2>}
      {loi && <p role="alert" className="text-red-800">{loi}</p>}
      {!duLieu && !loi && <p role="status">Đang tải cuộc bầu chọn…</p>}
      {duLieu?.laChuTri && !chiXem && (
        <details className="rounded-lg border border-slate-200 p-3">
          <summary className="cursor-pointer font-semibold">Tạo cuộc bầu chọn</summary>
          <form className="mt-3 space-y-3" onSubmit={async (event) => {
            event.preventDefault()
            const form = event.currentTarget
            if (await gui('', { tieuDe: new FormData(form).get('tieuDe'), ungVienIds: ungVienConCoMat, cacVong })) {
              form.reset()
              setUngVienIds([])
              setCacVong([{}])
            }
          }}>
            <fieldset className="space-y-2" disabled={dangGui}>
              <label className="block">Tiêu đề
                <input name="tieuDe" required maxLength={255} className="ml-2 rounded border px-2 py-1" />
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
                    <div key={i} className="grid gap-2 rounded-lg border p-3 sm:grid-cols-2">
                      <p className="font-semibold sm:col-span-2">Vòng {i + 1}</p>
                      {[
                        ['soNguoiDuocChon', 'Số người trúng vòng', i === 0 ? soCoTheChon : soCoTheChon - 1],
                        ['soLuaChonMoiPhieu', 'Số người mỗi cử tri chọn', soCoTheChon],
                      ].map(([khoa, ten, toiDa]) => (
                        <label key={khoa} className="grid gap-1">
                          {ten}
                          <input type="number" required min={1} max={Math.max(toiDa || 0, 1)}
                            className="rounded border px-2 py-1" value={vong[khoa] ?? ''}
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
              <button className={NUT} disabled={ungVienConCoMat.length === 0}>Tạo vòng 1</button>
            </fieldset>
          </form>
        </details>
      )}
      {duLieu?.cuocBau.length === 0 && <p>Cuộc họp chưa có cuộc bầu chọn.</p>}
      {duLieu?.cuocBau.map(({ cuocBau, cacVong }) => (
        <article className="space-y-3 rounded-lg border border-slate-200 p-3" key={cuocBau.cuocBauId}>
          <h3 className="font-bold">{cuocBau.tieuDe}</h3>
          {cacVong.map((muc, i) => <VongBau key={muc.vong.vongBauId} muc={muc}
            thanhVien={duLieu.thanhVien} laChuTri={duLieu.laChuTri} dangGui={dangGui} gui={gui} chiXem={chiXem}
            lanBauPhu={cacVong.slice(0, i).filter(({ vong }) => vong.soVong === muc.vong.soVong).length} />)}
        </article>
      ))}
    </div>
  )
}
