import { useState } from 'react'

export default function BanXemTruocVanBan({
  tenLoai, tieuDe, noiDung, trangThai, thongTin = [],
  tenCoQuan = import.meta.env.VITE_TEN_CO_QUAN, tenNguoiKy, ngayPhatHanh,
}) {
  const [hienViTriDau, setHienViTriDau] = useState(false)
  const tieuDeVanBan = tieuDe?.trim() || 'Chưa có tiêu đề'
  const ngay = ngayPhatHanh ? new Date(ngayPhatHanh) : null
  const diaDanh = import.meta.env.VITE_DIA_DANH_VAN_BAN?.trim()
  const ngayHienThi = ngay && !Number.isNaN(ngay.getTime())
    ? `Ngày ${ngay.getDate()} tháng ${ngay.getMonth() + 1} năm ${ngay.getFullYear()}`
    : null

  return (
    <section aria-label={`Xem trước ${tenLoai.toLowerCase()}`}
      className="min-w-0 overflow-hidden rounded-xl border border-slate-200 bg-slate-100">
      <header className="flex flex-wrap items-center gap-2 border-b border-slate-200 bg-white px-4 py-3">
        <h4 className="mr-auto text-sm font-bold text-[#102a43]">Xem trước A4</h4>
        {trangThai && <span className="rounded-md bg-blue-50 px-2 py-1 text-xs font-semibold text-blue-800">{trangThai}</span>}
        <button type="button" aria-pressed={hienViTriDau}
          onClick={() => setHienViTriDau((hien) => !hien)}
          className="rounded-md border border-slate-300 px-2 py-1 text-xs font-semibold text-slate-700 aria-pressed:border-red-300 aria-pressed:bg-red-50 aria-pressed:text-red-800">
          Vị trí con dấu
        </button>
      </header>
      <div className="vung-giay-a4 px-2 py-3 sm:px-4 sm:py-5">
        {hienViTriDau && (
          <p className="mx-auto mb-3 max-w-[210mm] rounded-md border border-red-200 bg-red-50 px-3 py-2 text-xs text-red-900">
            Vị trí tham khảo cho văn bản giấy. Dấu treo dùng khi cơ quan có quy định.
          </p>
        )}
        <div className="giay-a4 bg-white shadow-sm ring-1 ring-slate-200">
          <article className="noi-dung-giay-a4 leading-relaxed">
            <header className="grid grid-cols-[minmax(0,0.9fr)_minmax(0,1.1fr)] gap-4 text-center font-bold leading-snug">
              <div className="relative min-w-0">
                {tenCoQuan && <p className="break-words uppercase">{tenCoQuan}</p>}
                {hienViTriDau && (
                  <span className="relative -left-2 mt-1 inline-flex rounded-full border border-dashed border-red-700 px-2 py-1 text-[0.72em] font-normal text-red-800">
                    Vị trí dấu treo
                  </span>
                )}
              </div>
              <div className="min-w-0">
                <p>CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM</p>
                <p className="mt-1 inline-block border-b border-current pb-1">Độc lập - Tự do - Hạnh phúc</p>
                {ngayHienThi && <p className="mt-3 font-normal italic">{[diaDanh, ngayHienThi].filter(Boolean).join(', ')}</p>}
              </div>
            </header>
            <header className="mb-5 mt-7 text-center">
              <p className="font-bold uppercase">{tenLoai}</p>
              <h5 className="mt-1 break-words font-bold">{tieuDeVanBan}</h5>
            </header>
            {thongTin.length > 0 && (
              <dl className="mb-4 space-y-1.5">
                {thongTin.map(([nhan, giaTri]) => (
                  <div key={nhan} className="flex flex-wrap gap-x-1.5">
                    <dt className="font-bold">{nhan}</dt>
                    <dd className="min-w-0 break-words">{giaTri}</dd>
                  </div>
                ))}
              </dl>
            )}
            <p className="whitespace-pre-wrap break-words">{noiDung || 'Chưa có nội dung văn bản.'}</p>
            <footer className="ml-auto mt-8 w-full max-w-52 text-center">
              <p className="font-bold">Chủ trì cuộc họp</p>
              <div className="relative mx-auto mt-2 flex h-12 w-28 items-end justify-center border-b border-slate-300 text-[0.72em] italic text-slate-600">
                Vị trí ký
                {hienViTriDau && (
                  <span className="absolute -left-[12%] bottom-1 flex aspect-square w-[45%] items-center justify-center rounded-full border border-dashed border-red-700 bg-red-50/90 px-1 text-center leading-tight not-italic text-red-800">
                    Vị trí dấu
                  </span>
                )}
              </div>
              {tenNguoiKy && <p className="mt-2 break-words font-bold">{tenNguoiKy}</p>}
            </footer>
          </article>
        </div>
      </div>
    </section>
  )
}
