const LOP_NHAP = 'mt-1 block w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm font-normal'

export default function NoiDungThuMoiFields({ disabled }) {
  return (
    <fieldset disabled={disabled} className="space-y-3">
      <label className="block text-sm font-semibold">
        Tiêu đề email
        <input name="tieuDeThuMoi" className={LOP_NHAP} required maxLength={150} />
      </label>
      <label className="block text-sm font-semibold">
        Nội dung thư mời
        <textarea name="noiDungThuMoi" className={LOP_NHAP} required maxLength={4000} rows={5} />
      </label>
      <p className="text-xs leading-5 text-slate-600">
        Hệ thống tự đính kèm tên cuộc họp, thời gian và địa điểm hoặc link đã nhập.
      </p>
    </fieldset>
  )
}
