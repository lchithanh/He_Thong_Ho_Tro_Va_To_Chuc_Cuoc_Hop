import { useEffect, useRef, useState } from 'react'
import CalendarDays from 'lucide-react/dist/esm/icons/calendar-days.mjs'
import ChevronLeft from 'lucide-react/dist/esm/icons/chevron-left.mjs'
import ChevronRight from 'lucide-react/dist/esm/icons/chevron-right.mjs'
import Plus from 'lucide-react/dist/esm/icons/plus.mjs'
import Search from 'lucide-react/dist/esm/icons/search.mjs'
import { CHIEU_CAO_GIO, congNgay, dauThang, maNgay, trangThaiLich, xepLichTrongNgay } from './lichHopHienThi.js'

const NGAY_DAY_DU = new Intl.DateTimeFormat('vi-VN', { dateStyle: 'full' })
const THU_NGAN = new Intl.DateTimeFormat('vi-VN', { weekday: 'short' })
const NGAY_THANG = new Intl.DateTimeFormat('vi-VN', { day: 'numeric', month: 'numeric' })
const GIO_PHUT = new Intl.DateTimeFormat('vi-VN', { hour: '2-digit', minute: '2-digit', hourCycle: 'h23' })
const THANG_NAM = new Intl.DateTimeFormat('vi-VN', { month: 'long', year: 'numeric' })
const CAC_GIO = Array.from({ length: 24 }, (_, gio) => gio)
const LOP_NUT = 'inline-flex h-9 items-center justify-center rounded-lg text-sm font-medium text-slate-700 hover:bg-slate-100 focus-visible:outline-2 focus-visible:outline-blue-600'

function tenGio(gio) {
  return String(gio).padStart(2, '0') + ':00'
}

function ChonNgay({ ngayChon, onChonNgay }) {
  const [thangDangXem, setThangDangXem] = useState(null)
  const thang = thangDangXem ?? dauThang(ngayChon)
  const ngayDau = congNgay(thang, -((thang.getDay() + 6) % 7))
  const homNay = maNgay(new Date())

  function chuyenThang(soThang) {
    setThangDangXem(new Date(thang.getFullYear(), thang.getMonth() + soThang, 1))
  }

  return (
    <div className="rounded-lg border border-slate-200 p-2.5">
      <div className="mb-1 flex items-center justify-between gap-1">
        <span className="text-xs font-semibold capitalize" aria-live="polite">{THANG_NAM.format(thang)}</span>
        <div className="flex">
          <button type="button" className={LOP_NUT + ' size-8'} aria-label="Tháng trước" onClick={() => chuyenThang(-1)}><ChevronLeft size={15} /></button>
          <button type="button" className={LOP_NUT + ' size-8'} aria-label="Tháng sau" onClick={() => chuyenThang(1)}><ChevronRight size={15} /></button>
        </div>
      </div>
      <div className="grid grid-cols-7 justify-items-center gap-y-1 text-xs">
        {['T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'CN'].map((thu) => <span key={thu} className="text-slate-500">{thu}</span>)}
        {Array.from({ length: 42 }, (_, i) => {
          const ngay = congNgay(ngayDau, i)
          const id = maNgay(ngay)
          const duocChon = id === maNgay(ngayChon)
          return (
            <button key={id} type="button" aria-label={NGAY_DAY_DU.format(ngay)} aria-pressed={duocChon}
              aria-current={id === homNay ? 'date' : undefined}
              onClick={() => { setThangDangXem(null); onChonNgay(ngay) }}
              className={'grid size-7 place-items-center rounded-full focus-visible:outline-2 focus-visible:outline-blue-600 '
                + (duocChon ? 'bg-blue-600 text-white' : ngay.getMonth() === thang.getMonth() ? 'hover:bg-blue-50' : 'text-slate-400 hover:bg-slate-100')}>
              {ngay.getDate()}
            </button>
          )
        })}
      </div>
    </div>
  )
}

function LuoiLich({ ngayHienThi, lichTungNgay, ngayChon, bayGio, cheDo, onChonNgay, onXemLich, onMoDeXuat }) {
  const vungGio = useRef(null)
  const ngayHienTai = maNgay(bayGio)
  const ngayDangChon = maNgay(ngayChon)
  const gioDau = Math.min(7, ...lichTungNgay.flatMap((lich) => lich.map(({ batDau }) => Math.floor(batDau / 60))))
  const mauCot = { gridTemplateColumns: '48px repeat(' + ngayHienThi.length + ', minmax(0, 1fr))' }

  useEffect(() => {
    if (vungGio.current) vungGio.current.scrollTop = gioDau * CHIEU_CAO_GIO
  }, [gioDau, cheDo])

  return (
    <div ref={vungGio} role="region" tabIndex={0} aria-label="Lịch theo giờ"
      className="h-[min(620px,70dvh)] min-h-[340px] overflow-auto overscroll-contain focus-visible:outline-2 focus-visible:-outline-offset-2 focus-visible:outline-blue-600">
      <div style={{ minWidth: 48 + ngayHienThi.length * 118 }}>
        <div className="sticky top-0 z-20 grid border-b border-slate-200 bg-white text-center" style={mauCot}>
          <span aria-hidden="true" />
          {ngayHienThi.map((ngay) => {
            const id = maNgay(ngay)
            return (
              <button key={id} type="button" aria-label={NGAY_DAY_DU.format(ngay)} aria-pressed={id === ngayDangChon}
                aria-current={id === ngayHienTai ? 'date' : undefined} onClick={() => onChonNgay(ngay)}
                className={'border-l border-slate-100 px-1 py-3 text-sm focus-visible:outline-2 focus-visible:outline-blue-600 '
                  + (id === ngayDangChon ? 'bg-blue-50 font-semibold text-blue-700' : 'text-slate-600 hover:bg-slate-50')}>
                <span className="capitalize">{THU_NGAN.format(ngay)}</span> {ngay.getDate()}
              </button>
            )
          })}
        </div>
        <div className="grid" style={mauCot}>
          <div className="sticky left-0 z-10 bg-white" aria-hidden="true">
            {CAC_GIO.map((gio) => (
              <div key={gio} className="pr-1.5 pt-1 text-right text-[11px] tabular-nums text-slate-500" style={{ height: CHIEU_CAO_GIO }}>
                {tenGio(gio)}
              </div>
            ))}
          </div>
          {ngayHienThi.map((ngay, i) => (
            <div key={maNgay(ngay)} className="relative min-w-0 border-l border-slate-200">
              {CAC_GIO.map((gio) => (
                <div key={gio} className="border-b border-slate-200" style={{ height: CHIEU_CAO_GIO }}>
                  {onMoDeXuat && (
                    <button type="button" tabIndex={-1} aria-label={'Đề xuất lịch lúc ' + tenGio(gio) + ', ' + NGAY_DAY_DU.format(ngay)}
                      onClick={() => onMoDeXuat(ngay, gio)} className="grid size-full place-items-center text-transparent hover:bg-blue-50/75 hover:text-blue-600">
                      <Plus size={15} aria-hidden="true" />
                    </button>
                  )}
                </div>
              ))}
              {lichTungNgay[i].map(({ lich, batDau, ketThuc, cot, soCot }) => (
                <button key={lich.lichHopId} type="button" title={lich.tieuDeCuocHop}
                  aria-label={'Xem chi tiết ' + (lich.tieuDeCuocHop || 'lịch #' + lich.lichHopId)}
                  onClick={() => onXemLich(ngay, lich.lichHopId)}
                  className={'absolute z-[1] overflow-hidden rounded-md border border-white px-1.5 py-0.5 text-left text-[11px] leading-tight hover:brightness-95 focus-visible:z-20 focus-visible:outline-2 focus-visible:outline-blue-600 ' + trangThaiLich(lich).the}
                  style={{ top: batDau * CHIEU_CAO_GIO / 60, height: Math.max(20, (ketThuc - batDau) * CHIEU_CAO_GIO / 60),
                    left: cot * 100 / soCot + '%', width: 100 / soCot + '%' }}>
                  <span className="block truncate font-semibold">{lich.tieuDeCuocHop || 'Lịch #' + lich.lichHopId}</span>
                  <span className="block truncate">{lich.thoiGianBatDau.slice(11, 16)} · {trangThaiLich(lich).ten}</span>
                </button>
              ))}
              {maNgay(ngay) === ngayHienTai && (
                <div aria-hidden="true" className="pointer-events-none absolute inset-x-0 z-[2] border-t-2 border-emerald-500"
                  style={{ top: (bayGio.getHours() + bayGio.getMinutes() / 60) * CHIEU_CAO_GIO }}>
                  <span className="absolute -left-0.5 -top-1 size-2 rounded-full bg-emerald-500" />
                </div>
              )}
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}

export default function LichCuocHop({ danhSach, loiTai, ngayChon, onChonNgay, onMoDeXuat, renderChiTiet }) {
  const [cheDo, setCheDo] = useState('tuan-lam-viec')
  const [tuKhoa, setTuKhoa] = useState('')
  const [moLichNho, setMoLichNho] = useState(false)
  const [lichDangXemId, setLichDangXemId] = useState(null)
  const [bayGio, setBayGio] = useState(() => new Date())
  const vungChiTiet = useRef(null)
  const thu = (ngayChon.getDay() + 6) % 7
  const dauTuan = congNgay(ngayChon, -thu)
  const ngayHienThi = cheDo === 'ngay' ? [ngayChon]
    : Array.from({ length: cheDo === 'ca-tuan' ? 7 : 5 }, (_, i) => congNgay(dauTuan, i))
  const tuKhoaChuan = tuKhoa.trim().toLocaleLowerCase('vi-VN')
  const danhSachLoc = (danhSach ?? []).filter((lich) =>
    [lich.tieuDeCuocHop, lich.tenPhongHop].join(' ').toLocaleLowerCase('vi-VN').includes(tuKhoaChuan),
  )
  const lichTungNgay = ngayHienThi.map((ngay) => xepLichTrongNgay(danhSachLoc, ngay))
  const lichNgayChon = (lichTungNgay[cheDo === 'ngay' ? 0 : thu] ?? xepLichTrongNgay(danhSachLoc, ngayChon)).map(({ lich }) => lich)
  const lichDangXem = lichNgayChon.find(({ lichHopId }) => lichHopId === lichDangXemId)
  const laHomNay = maNgay(ngayChon) === maNgay(bayGio)
  const khoangNgay = cheDo === 'ngay' ? NGAY_THANG.format(ngayChon)
    : NGAY_THANG.format(ngayHienThi[0]) + ' – ' + NGAY_THANG.format(ngayHienThi.at(-1))

  useEffect(() => {
    const timer = setInterval(() => setBayGio(new Date()), 60000)
    return () => clearInterval(timer)
  }, [])

  useEffect(() => {
    vungChiTiet.current?.focus()
  }, [lichDangXemId])

  function chonNgay(ngay) {
    setLichDangXemId(null)
    onChonNgay(ngay)
  }

  function moLich(ngay, id) {
    onChonNgay(ngay)
    setLichDangXemId(id)
  }

  function chuyenKhoang(huong) {
    chonNgay(congNgay(ngayChon, huong * (cheDo === 'ngay' ? 1 : 7)))
  }

  return (
    <section aria-label="Lịch họp" className="min-w-0">
      <div className="grid min-w-0 overflow-hidden rounded-xl border border-slate-200 bg-white xl:grid-cols-[230px_minmax(0,1fr)]">
        <aside aria-label="Lịch trong ngày" className="min-w-0 border-b border-slate-200 px-4 py-3 xl:border-r xl:border-b-0">
          <div className="mb-3 flex items-center justify-between gap-2">
            <button type="button" aria-expanded={moLichNho} onClick={() => setMoLichNho(!moLichNho)} className={LOP_NUT + ' gap-2 px-2'}>
              <CalendarDays size={16} aria-hidden="true" /> Chọn ngày
            </button>
            {onMoDeXuat && (
              <button type="button" aria-label="Đề xuất lịch họp" onClick={() => onMoDeXuat(ngayChon, 8)}
                className="grid size-8 place-items-center rounded-lg bg-blue-600 text-white hover:bg-blue-700 focus-visible:outline-2 focus-visible:outline-blue-700">
                <Plus size={18} aria-hidden="true" />
              </button>
            )}
          </div>
          {moLichNho && (
            <ChonNgay ngayChon={ngayChon} onChonNgay={(ngay) => {
              chonNgay(ngay)
              if (cheDo === 'tuan-lam-viec' && (ngay.getDay() === 0 || ngay.getDay() === 6)) setCheDo('ngay')
              setMoLichNho(false)
            }} />
          )}
          <div className="mt-4 text-center xl:mt-6">
            <h2 className="text-sm font-semibold capitalize text-slate-900">{THANG_NAM.format(ngayChon)}</h2>
            <p className="mt-1 text-sm font-medium text-blue-700" aria-live="polite">
              {laHomNay ? 'Hôm nay, ' : ''}{THU_NGAN.format(ngayChon)} {NGAY_THANG.format(ngayChon)}
            </p>
          </div>
          {laHomNay && (
            <div className="mt-4 flex items-center gap-2 text-xs font-semibold text-emerald-700">
              <span>{GIO_PHUT.format(bayGio)}</span>
              <span className="h-0.5 flex-1 rounded-full bg-emerald-500" aria-hidden="true" />
            </div>
          )}
          <div className="mt-4 max-h-48 overflow-auto xl:max-h-[350px]">
            {loiTai ? <p role="alert" className="text-sm text-red-800">{loiTai}</p>
              : danhSach === null ? <p role="status" className="text-sm text-slate-600">Đang tải lịch…</p>
                : !lichNgayChon.length && <p className="py-2 text-sm text-slate-500">{tuKhoaChuan ? 'Không có lịch khớp trong ngày.' : 'Ngày này chưa có lịch họp.'}</p>}
            <ul className="space-y-1.5">
              {lichNgayChon.map((lich) => (
                <li key={lich.lichHopId}>
                  <button type="button" aria-pressed={lich.lichHopId === lichDangXemId}
                    onClick={() => moLich(ngayChon, lich.lichHopId)}
                    className="block w-full rounded-lg bg-slate-50 px-2.5 py-2 text-left text-xs hover:bg-blue-50 aria-pressed:bg-blue-50 focus-visible:outline-2 focus-visible:outline-blue-600">
                    <span className="block text-slate-600">{lich.thoiGianBatDau.slice(11, 16)} – {lich.thoiGianKetThuc.slice(11, 16)}</span>
                    <span className="mt-0.5 block break-words font-semibold text-slate-900">{lich.tieuDeCuocHop || 'Lịch #' + lich.lichHopId}</span>
                    <span className="mt-0.5 block text-slate-600">{lich.tenPhongHop || trangThaiLich(lich).ten}</span>
                  </button>
                </li>
              ))}
            </ul>
          </div>
        </aside>
        <div className="min-w-0">
          <div className="flex flex-wrap items-center gap-1.5 border-b border-slate-200 px-3 py-2">
            <h2 className="mr-2 whitespace-nowrap text-sm font-semibold tabular-nums text-slate-900" aria-live="polite">{khoangNgay}</h2>
            <button type="button" onClick={() => chonNgay(new Date())} className={LOP_NUT + ' bg-slate-100 px-3'}>Hôm nay</button>
            <button type="button" aria-label={cheDo === 'ngay' ? 'Ngày trước' : 'Tuần trước'} onClick={() => chuyenKhoang(-1)} className={LOP_NUT + ' w-8'}><ChevronLeft size={18} /></button>
            <button type="button" aria-label={cheDo === 'ngay' ? 'Ngày sau' : 'Tuần sau'} onClick={() => chuyenKhoang(1)} className={LOP_NUT + ' w-8'}><ChevronRight size={18} /></button>
            <div className="flex flex-1 flex-wrap items-center justify-end gap-2">
              <label className="flex h-9 min-w-32 flex-1 items-center gap-1.5 rounded-lg border border-slate-200 px-2 text-slate-500 sm:max-w-48">
                <Search size={15} aria-hidden="true" />
                <input type="search" aria-label="Tìm cuộc họp hoặc phòng" placeholder="Tìm cuộc họp, phòng" value={tuKhoa}
                  onChange={(event) => { setTuKhoa(event.target.value); setLichDangXemId(null) }}
                  className="min-w-0 flex-1 text-sm text-slate-900 outline-none" />
              </label>
              <select aria-label="Chế độ lịch" value={cheDo} onChange={(event) => setCheDo(event.target.value)}
                className="h-9 max-w-full rounded-lg border border-slate-200 bg-white px-2 text-sm text-slate-700 focus-visible:outline-blue-600">
                <option value="tuan-lam-viec">Tuần làm việc</option>
                <option value="ca-tuan">Cả tuần</option>
                <option value="ngay">Ngày</option>
              </select>
            </div>
          </div>
          <LuoiLich ngayHienThi={ngayHienThi} lichTungNgay={lichTungNgay} ngayChon={ngayChon} bayGio={bayGio} cheDo={cheDo}
            onChonNgay={chonNgay} onXemLich={moLich} onMoDeXuat={onMoDeXuat && ((ngay, gio) => {
              chonNgay(ngay)
              onMoDeXuat(ngay, gio)
            })} />
        </div>
      </div>
      {lichDangXem && (
        <section ref={vungChiTiet} tabIndex={-1} aria-label="Chi tiết lịch họp" className="mt-4 scroll-mt-24 rounded-xl border border-slate-200 bg-white p-4">
          <div className="mb-3 flex flex-wrap items-center justify-between gap-2">
            <div>
              <h2 className="font-semibold">{lichDangXem.tieuDeCuocHop || 'Lịch #' + lichDangXem.lichHopId}</h2>
              <p className="mt-1 text-sm text-slate-600">
                {new Date(lichDangXem.thoiGianBatDau).toLocaleString('vi-VN')} – {new Date(lichDangXem.thoiGianKetThuc).toLocaleString('vi-VN')}
              </p>
            </div>
            <button type="button" onClick={() => setLichDangXemId(null)} className={LOP_NUT + ' px-2'}>Đóng chi tiết</button>
          </div>
          {renderChiTiet(lichDangXem)}
        </section>
      )}
    </section>
  )
}
