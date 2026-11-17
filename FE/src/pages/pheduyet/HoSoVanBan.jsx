import { useState } from 'react'
import KeHoachChoDuyet from './KeHoachChoDuyet.jsx'
import BienBanChoDuyet from './BienBanChoDuyet.jsx'
import BienBanChoPhatHanh from './BienBanChoPhatHanh.jsx'
import KetLuanChoPhatHanh from './KetLuanChoPhatHanh.jsx'

const MUC = {
  'ke-hoach': { ten: 'Cuộc họp và lịch', Trang: KeHoachChoDuyet },
  'bien-ban-duyet': { ten: 'Biên bản', Trang: BienBanChoDuyet },
  'bien-ban-phat-hanh': { ten: 'Biên bản', Trang: BienBanChoPhatHanh },
  'ket-luan': { ten: 'Kết luận', Trang: KetLuanChoPhatHanh },
}

function HoSoVanBan({ cheDo, nguoiDung, ...props }) {
  const laQuanLy = nguoiDung.maVaiTro === 'LANH_DAO' || nguoiDung.truongPhong
  const cacMuc = cheDo === 'duyet'
    ? [...(laQuanLy ? ['ke-hoach'] : []), 'bien-ban-duyet', 'ket-luan']
    : ['bien-ban-phat-hanh', 'ket-luan']
  const [mucHienTai, setMucHienTai] = useState(cacMuc[0])
  const Trang = MUC[mucHienTai].Trang
  const tieuDe = cheDo === 'duyet' ? 'Hồ sơ chờ duyệt' : 'Văn bản chờ phát hành'

  return (
    <div className="space-y-4">
      <header className="rounded-xl border border-slate-200 bg-white px-5 pt-4">
        <h1 className="text-xl font-bold text-slate-900">{tieuDe}</h1>
        <nav aria-label={tieuDe} className="mt-3 flex flex-wrap gap-2">
          {cacMuc.map((id) => (
            <button type="button" key={id} aria-pressed={id === mucHienTai}
              onClick={() => setMucHienTai(id)}
              className={`border-b-2 px-3 py-2.5 text-sm font-semibold ${mucHienTai === id
                ? 'border-emerald-700 text-emerald-800'
                : 'border-transparent text-slate-600 hover:text-slate-900'}`}>
              {MUC[id].ten}
            </button>
          ))}
        </nav>
      </header>
      <Trang key={mucHienTai} {...props} nguoiDung={nguoiDung} cheDo={cheDo} />
    </div>
  )
}

export function HoSoChoDuyet(props) {
  return <HoSoVanBan {...props} cheDo="duyet" />
}

export function VanBanChoPhatHanh(props) {
  return <HoSoVanBan {...props} cheDo="phat-hanh" />
}
