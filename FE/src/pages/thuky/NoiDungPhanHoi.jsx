export default function NoiDungPhanHoi({ phanHoi }) {
  return (
    <div className="space-y-1 rounded-lg bg-slate-50 p-3 text-sm">
      <p className="whitespace-pre-wrap break-words"><strong>Lý do:</strong> {phanHoi.lyDo || 'Chưa có lý do được ghi nhận.'}</p>
      <p className="text-xs text-slate-600">
        {[phanHoi.nguoiXuLy, phanHoi.thoiGian && new Date(phanHoi.thoiGian).toLocaleString('vi-VN')].filter(Boolean).join(' · ')}
      </p>
    </div>
  )
}
