import { useEffect, useState } from 'react'
import TepHoSo from '../../components/TepHoSo.jsx'
import { phanHoiLoiMoiKhach, xemLoiMoiKhach } from '../../api/nghiepVuCuocHopApi.js'

const NUT = 'rounded-lg border border-slate-300 px-4 py-2.5 text-sm font-semibold disabled:opacity-60'
const KHUNG = 'space-y-4 rounded-xl border border-slate-200 bg-white p-5'
const DINH_DANG = new Intl.DateTimeFormat('vi-VN', {
  timeZone: 'Asia/Ho_Chi_Minh', dateStyle: 'short', timeStyle: 'short',
})
const TUONG_DOI = new Intl.RelativeTimeFormat('vi-VN')

function mocGio(giaTri) {
  return new Date(giaTri + '+07:00')
}

function thoiGian(giaTri) {
  return giaTri ? DINH_DANG.format(mocGio(giaTri)) : null
}

function tuLucCapNhat(giaTri, bayGio) {
  const phut = Math.max(0, Math.floor((bayGio - mocGio(giaTri)) / 60000))
  if (phut < 1) return 'Vừa cập nhật'
  if (phut < 60) return TUONG_DOI.format(-phut, 'minute')
  if (phut < 1440) return TUONG_DOI.format(-Math.floor(phut / 60), 'hour')
  return TUONG_DOI.format(-Math.floor(phut / 1440), 'day')
}

export default function LoiMoiKhachNgoai() {
  const [loiMoi, setLoiMoi] = useState(null)
  const [loi, setLoi] = useState('')
  const [dangGui, setDangGui] = useState(false)
  const [lanTai, setLanTai] = useState(0)
  const [bayGio, setBayGio] = useState(Date.now)
  const [ma, setMa] = useState(() => window.location.hash.slice(1))

  useEffect(() => {
    function doiLienKet() {
      setLoiMoi(null)
      setLoi('')
      setMa(window.location.hash.slice(1))
    }
    window.addEventListener('hashchange', doiLienKet)
    return () => window.removeEventListener('hashchange', doiLienKet)
  }, [])

  useEffect(() => {
    const controller = new AbortController()
    xemLoiMoiKhach(ma, controller.signal)
      .then((duLieu) => {
        if (controller.signal.aborted) return
        setLoiMoi(duLieu)
        setLoi('')
        setBayGio(Date.now())
      })
      .catch((error) => {
        if (controller.signal.aborted) return
        if ([403, 404].includes(error.status)) setLoiMoi(null)
        setLoi(error.message)
      })
    return () => controller.abort()
  }, [ma, lanTai])

  useEffect(() => {
    if (dangGui) return
    const hen = setInterval(() => setLanTai((lan) => lan + 1), 60000)
    return () => clearInterval(hen)
  }, [dangGui])

  async function phanHoi(thamDu) {
    setDangGui(true)
    setLoi('')
    try {
      setLoiMoi(await phanHoiLoiMoiKhach(ma, thamDu))
    } catch (error) {
      setLoi(error.message)
    } finally {
      setDangGui(false)
    }
  }

  const lichSu = loiMoi?.lichSu ?? []
  const moiNhat = lichSu[0]
  const trangThaiLich = {
    CHO_DOI_LICH: 'Đang chờ xác nhận lịch mới',
    DA_HUY: 'Lịch đã hủy hoặc chưa có giờ họp được xác nhận',
    DA_XAC_NHAN: 'Lịch họp đã xác nhận',
  }[loiMoi?.trangThaiLich]

  return (
    <main translate="no" lang="vi" className="min-h-screen bg-slate-100 px-4 py-6 text-slate-950 sm:py-10">
      <article className="mx-auto max-w-5xl space-y-4">
        <header className="space-y-3 rounded-2xl bg-[#0f2744] p-6 text-white sm:p-8">
          <p className="text-sm font-medium text-slate-200">Hồ sơ dành cho khách mời{loiMoi?.maCuocHop && ` · ${loiMoi.maCuocHop}`}</p>
          <h1 className="break-words text-2xl font-bold sm:text-3xl">{loiMoi?.tieuDe || 'Lời mời họp'}</h1>
          {loiMoi && <p>Khách mời · <strong>{loiMoi.hoTen}</strong></p>}
        </header>
        {loi && <p role="alert" className="rounded-lg bg-white p-4 text-red-800">{loi}</p>}
        {!loiMoi && !loi && <p role="status">Đang tải hồ sơ cuộc họp…</p>}
        {loiMoi && <>
          <section className={KHUNG}>
            <div className="flex flex-wrap items-center justify-between gap-2">
              <div>
                <h2 className="font-bold">Lịch và địa điểm</h2>
                <p role="status" className={loiMoi.trangThaiLich === 'DA_XAC_NHAN' ? 'text-emerald-800' : 'text-amber-900'}>{trangThaiLich}</p>
              </div>
              <button className={NUT} type="button" onClick={() => setLanTai((lan) => lan + 1)}>Tải cập nhật</button>
            </div>
            <dl className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
              {[
                ['Bắt đầu (giờ Việt Nam)', thoiGian(loiMoi.batDau)],
                ['Giờ bắt đầu đề xuất', thoiGian(loiMoi.batDauDeXuat)],
                ['Kết thúc (giờ Việt Nam)', thoiGian(loiMoi.ketThuc)],
                ['Giờ kết thúc đề xuất', thoiGian(loiMoi.ketThucDeXuat)],
                ['Địa điểm', loiMoi.diaDiem],
                ['Hình thức', { TRUC_TIEP: 'Trực tiếp', TRUC_TUYEN: 'Trực tuyến' }[loiMoi.loaiHinh]],
                ['Chủ trì', loiMoi.chuTri],
              ].filter(([, giaTri]) => giaTri).map(([nhan, giaTri]) => (
                <div key={nhan} className="rounded-lg bg-slate-50 p-3">
                  <dt className="text-xs text-slate-600">{nhan}</dt><dd className="font-semibold">{giaTri}</dd>
                </div>
              ))}
            </dl>
            {loiMoi.duongDanHop && <a href={loiMoi.duongDanHop} target="_blank" rel="noreferrer"
              className="font-semibold text-emerald-800 underline">Mở phòng họp trực tuyến</a>}
            {loiMoi.duocPhanHoi && <div className="flex flex-wrap gap-2">
              <button className={NUT + ' bg-emerald-800 text-white'} type="button" disabled={dangGui}
                onClick={() => phanHoi(true)}>Xác nhận tham dự</button>
              <button className={NUT} type="button" disabled={dangGui}
                onClick={() => phanHoi(false)}>Từ chối tham dự</button>
            </div>}
            {loiMoi.trangThaiMoi === 'DONG_Y' && <p role="status">Bạn đã xác nhận tham dự.</p>}
            {loiMoi.trangThaiMoi === 'TU_CHOI' && <p role="status">Bạn đã từ chối tham dự.</p>}
          </section>
          <div className="grid items-start gap-4 lg:grid-cols-[minmax(0,2fr)_minmax(0,1fr)]">
            <div className="space-y-4">
              {(loiMoi.mucTieu || loiMoi.noiDung) && <section className={KHUNG}>
                <h2 className="font-bold">Nội dung cuộc họp</h2>
                {loiMoi.mucTieu && <div><h3 className="font-semibold">Mục tiêu</h3><p className="whitespace-pre-wrap break-words">{loiMoi.mucTieu}</p></div>}
                {loiMoi.noiDung && <p className="whitespace-pre-wrap break-words">{loiMoi.noiDung}</p>}
              </section>}
              <section className={KHUNG}>
                <h2 className="font-bold">Thư mời và tài liệu</h2>
                {loiMoi.thuMoi && <div className="space-y-2">
                  <h3 className="font-semibold">{loiMoi.thuMoi.tieuDe}</h3>
                  <p className="whitespace-pre-wrap break-words">{loiMoi.thuMoi.noiDung}</p>
                  {loiMoi.thuMoi.tenTep && <TepHoSo key={`giay-moi-${moiNhat?.capNhatId}`} maMoi={ma} ten={loiMoi.thuMoi.tenTep}
                    xemPdf={loiMoi.thuMoi.kieuTep === 'application/pdf'} duongDan="/api/loi-moi-khach/giay-moi" />}
                </div>}
                {(loiMoi.taiLieu ?? []).map((tep) => (
                  <div key={tep.taiLieuId} className="space-y-2 border-t border-slate-100 pt-3">
                    <p className="text-xs text-slate-600">{tep.nhom}</p>
                    <p className="break-words font-semibold">{tep.tenTaiLieu}</p>
                    <TepHoSo maMoi={ma} ten={tep.tenTaiLieu} xemPdf={tep.dinhDang === 'pdf'}
                      duongDan={`/api/loi-moi-khach/tai-lieu/${tep.taiLieuId}`} />
                  </div>
                ))}
                {!loiMoi.thuMoi && !loiMoi.taiLieu?.length && <p>Chưa có tệp được chia sẻ.</p>}
              </section>
            </div>
            <aside className={KHUNG}>
              <h2 className="font-bold">Lịch sử cập nhật</h2>
              {moiNhat && <p className="text-sm text-slate-600">{tuLucCapNhat(moiNhat.ngayCapNhat, bayGio)}</p>}
              {lichSu.length > 0 ? <ul className="space-y-3 border-l border-slate-200 pl-4">
                {lichSu.map((muc) => <li key={muc.capNhatId}>
                  <p className="font-medium">{muc.noiDung}</p>
                  <p className="text-sm text-slate-600">{thoiGian(muc.ngayCapNhat)}</p>
                </li>)}
              </ul> : <p>Chưa có cập nhật được ghi lại.</p>}
            </aside>
          </div>
        </>}
      </article>
    </main>
  )
}
