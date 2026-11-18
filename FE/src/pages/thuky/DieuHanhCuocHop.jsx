import { useState } from 'react'
import CirclePlay from 'lucide-react/dist/esm/icons/circle-play.mjs'
import Radio from 'lucide-react/dist/esm/icons/radio.mjs'
import CircleCheck from 'lucide-react/dist/esm/icons/circle-check.mjs'

const GIAI_DOAN = {
  'chuan-bi': { ten: 'Chuẩn bị cuộc họp', BieuTuong: CirclePlay, lop: 'border-blue-200 bg-blue-50 text-blue-950' },
  'dang-hop': { ten: 'Cuộc họp đang diễn ra', BieuTuong: Radio, lop: 'border-amber-200 bg-amber-50 text-amber-950' },
  'ket-thuc': { ten: 'Cuộc họp đã kết thúc', BieuTuong: CircleCheck, lop: 'border-slate-200 bg-slate-50 text-slate-900' },
}

const LOP_TAT = 'rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm font-semibold text-slate-800 hover:bg-slate-50'

function hienThiGio(moc) {
  return new Date(moc).toLocaleString('vi-VN')
}

export default function DieuHanhCuocHop({
  giaiDoan, chiTiet, coLichXacNhan, coQuyenDieuHanh, dangBatDau, dangKetThuc,
  loiBatDau, loiKetThuc, onBatDau, onKetThuc, thaoTac,
}) {
  const [xacNhanKetThuc, setXacNhanKetThuc] = useState(false)
  const { ten, BieuTuong, lop } = GIAI_DOAN[giaiDoan]
  const { thoiGianBatDauThucTe, thoiGianKetThucThucTe } = chiTiet

  return (
    <section aria-label={ten} className={`space-y-3 rounded-xl border p-4 ${lop}`}>
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div className="flex min-w-0 items-start gap-3">
          <BieuTuong size={22} className="mt-0.5 shrink-0" aria-hidden="true" />
          <div>
            <h2 className="font-bold" aria-live="polite">{ten}</h2>
            {giaiDoan === 'chuan-bi' && (
              <p className="mt-0.5 text-sm">{coLichXacNhan ? 'Kế hoạch đã duyệt, lịch họp đã xác nhận.' : 'Cần có lịch họp được xác nhận trước khi bắt đầu.'}</p>
            )}
            {thoiGianBatDauThucTe && <p className="mt-0.5 text-sm">Bắt đầu thực tế · {hienThiGio(thoiGianBatDauThucTe)}</p>}
            {thoiGianKetThucThucTe && <p className="mt-0.5 text-sm">Kết thúc thực tế · {hienThiGio(thoiGianKetThucThucTe)}</p>}
            {giaiDoan === 'ket-thuc' && (
              <p className="mt-0.5 text-sm">{chiTiet.daHoanTatHoSo ? 'Hồ sơ đã hoàn tất.' : 'Hoàn thiện biên bản và kết luận trong hồ sơ cuộc họp.'}</p>
            )}
          </div>
        </div>
        {coQuyenDieuHanh && giaiDoan === 'chuan-bi' && (
          <button type="button" onClick={onBatDau} disabled={!coLichXacNhan || dangBatDau}
            className="rounded-lg bg-blue-700 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-800 disabled:opacity-50">
            {dangBatDau ? 'Đang bắt đầu…' : 'Bắt đầu cuộc họp'}
          </button>
        )}
        {coQuyenDieuHanh && giaiDoan === 'dang-hop' && !xacNhanKetThuc && (
          <button type="button" onClick={() => setXacNhanKetThuc(true)} disabled={dangKetThuc}
            className="rounded-lg border border-red-300 bg-white px-4 py-2 text-sm font-semibold text-red-800 hover:bg-red-50 disabled:opacity-50">
            {dangKetThuc ? 'Đang kết thúc…' : 'Kết thúc cuộc họp'}
          </button>
        )}
      </div>
      {thaoTac.length > 0 && (
        <div className="flex flex-wrap gap-2" aria-label="Thao tác cuộc họp">
          {thaoTac.map(({ ten: nhan, onClick }) => (
            <button key={nhan} type="button" className={LOP_TAT} onClick={onClick}>{nhan}</button>
          ))}
        </div>
      )}
      {giaiDoan === 'dang-hop' && xacNhanKetThuc && (
        <div className="space-y-2 rounded-lg border border-red-200 bg-white p-3">
          <p className="text-sm font-medium">Xác nhận kết thúc và chuyển sang hoàn thiện văn bản sau họp?</p>
          <div className="flex flex-wrap gap-2">
            <button type="button" className={LOP_TAT} onClick={() => setXacNhanKetThuc(false)}>Tiếp tục họp</button>
            <button type="button" className="rounded-lg bg-red-700 px-3 py-2 text-sm font-semibold text-white hover:bg-red-800"
              onClick={() => { setXacNhanKetThuc(false); onKetThuc() }}>
              Xác nhận kết thúc
            </button>
          </div>
        </div>
      )}
      {(loiBatDau || loiKetThuc) && <p role="alert" className="text-sm text-red-800">{loiBatDau || loiKetThuc}</p>}
    </section>
  )
}
