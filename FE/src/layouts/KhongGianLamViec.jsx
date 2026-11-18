import { useState } from 'react'
import BoCucChung from './BoCucChung.jsx'
import TongQuanCuocHop from '../pages/TongQuanCuocHop.jsx'
import { LOP_NUT_CHINH } from '../components/lopBieuMau.js'
import ChiTietDeXuat from '../pages/thuky/ChiTietDeXuat.jsx'
import { layMenuKhongGianLamViec } from './menuKhongGianLamViec.js'

export default function KhongGianLamViec({ nguoiDung, loi, onDangXuat }) {
  const { maVaiTro, token } = nguoiDung
  const laQuanLy = maVaiTro === 'LANH_DAO' || (maVaiTro === 'CAN_BO' && nguoiDung.truongPhong)
  const [mucDangChon, setMucDangChon] = useState('tong-quan')
  const [cuocHopLapLich, setCuocHopLapLich] = useState(null)
  const [hoSoDangXem, setHoSoDangXem] = useState(null)
  const [cuocHopCanDuyet, setCuocHopCanDuyet] = useState(null)
  const cacMuc = layMenuKhongGianLamViec(maVaiTro, laQuanLy)

  const mucHienTai = cacMuc.find((muc) => muc.id === mucDangChon)
  const TrangHienTai = mucHienTai?.Trang

  function chonMuc(id) {
    setHoSoDangXem(null)
    setCuocHopCanDuyet(null)
    setMucDangChon(id)
  }

  function moYeuCauDuyet(cuocHopId) {
    chonMuc('ho-so-cho-duyet')
    setCuocHopCanDuyet(cuocHopId)
  }

  return (
    <BoCucChung
      nguoiDung={nguoiDung}
      onDangXuat={onDangXuat}
      dieuHuong={cacMuc}
      mucDangChon={mucDangChon}
      onChonMuc={chonMuc}
    >
      {loi && (
        <p role="alert" className="rounded-xl border border-red-200 bg-red-50 p-4 text-sm text-red-800">
          {loi}
        </p>
      )}

      {!hoSoDangXem && !TrangHienTai && laQuanLy && (
        <section className="flex flex-wrap items-center justify-between gap-3 rounded-xl border border-slate-200 bg-white p-4">
          <div>
            <h2 className="font-semibold">{maVaiTro === 'LANH_DAO' ? 'Không gian lãnh đạo' : 'Không gian trưởng phòng'}</h2>
            <p className="mt-1 text-sm text-slate-600">
              Thư ký mặc định · {nguoiDung.tenThuKyMacDinh || 'Chưa được phân công'}
            </p>
          </div>
          <button type="button" className={LOP_NUT_CHINH} onClick={() => setMucDangChon('de-xuat-cua-toi')}>Đề xuất cuộc họp</button>
        </section>
      )}

      {hoSoDangXem ? (
        <ChiTietDeXuat key={hoSoDangXem} cuocHopId={hoSoDangXem} token={token} nguoiDung={nguoiDung}
          chiXem tabBanDau="thong-tin" onMoYeuCauDuyet={moYeuCauDuyet} onQuayLai={() => setHoSoDangXem(null)} />
      ) : TrangHienTai ? (
        <TrangHienTai
          key={mucDangChon}
          onXemCuocHop={laQuanLy ? setHoSoDangXem : undefined}
          token={token}
          nguoiDung={nguoiDung}
          cuocHopCanDuyet={cuocHopCanDuyet}
          chiXemBanGiao={mucDangChon === 'cuoc-hop-duoc-giao'}
          chiXemChuyenCap={mucDangChon === 'ho-so-chuyen-cap'}
          chiXemRaSoat={mucDangChon === 'ho-so-ra-soat'}
          chiXemPhanHoi={mucDangChon === 'phan-hoi-ke-hoach'}
          cuocHopLapLich={cuocHopLapLich}
          onLapLich={(id) => {
            setCuocHopLapLich(id)
            setMucDangChon('lich-hop')
          }}
        />
      ) : (
        <TongQuanCuocHop
          token={token}
          nguoiDung={nguoiDung}
          onMoHoSoDangHop={setHoSoDangXem}
          onMoBieuQuyet={cacMuc.some(({ id }) => id === 'tham-gia-bieu-quyet')
            ? () => chonMuc('tham-gia-bieu-quyet') : undefined}
          coTheMoDanhSach={cacMuc.some((muc) => muc.id === 'de-xuat')}
          onMoDanhSach={() => setMucDangChon('de-xuat')}
          onXemCuocHop={laQuanLy ? setHoSoDangXem : undefined}
        />
      )}
    </BoCucChung>
  )
}
