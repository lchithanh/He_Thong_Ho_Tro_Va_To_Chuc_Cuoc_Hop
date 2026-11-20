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
  const [lichDangMoTuHoSo, setLichDangMoTuHoSo] = useState(null)
  const [hoSoDangXem, setHoSoDangXem] = useState(null)
  const cacMuc = layMenuKhongGianLamViec(maVaiTro, laQuanLy)

  const mucHienTai = cacMuc.find((muc) => muc.id === mucDangChon)
  const TrangHienTai = mucHienTai?.Trang

  function chonMuc(id) {
    setHoSoDangXem(null)
    setLichDangMoTuHoSo(null)
    setMucDangChon(id)
  }

  function xemHoSo(id) {
    setHoSoDangXem({ id, tab: 'thong-tin', chiXem: true })
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
        <ChiTietDeXuat key={hoSoDangXem.id} cuocHopId={hoSoDangXem.id} token={token} nguoiDung={nguoiDung}
          chiXem={hoSoDangXem.chiXem} tabBanDau={hoSoDangXem.tab} onQuayLai={() => setHoSoDangXem(null)}
          onLapLich={(id) => {
            setLichDangMoTuHoSo({ id, mucTruoc: mucDangChon })
            setHoSoDangXem(null)
            setMucDangChon('lich-hop')
          }} />
      ) : TrangHienTai ? (
        <TrangHienTai
          key={mucDangChon}
          onXemCuocHop={laQuanLy ? xemHoSo : undefined}
          token={token}
          nguoiDung={nguoiDung}
          chiXemBanGiao={mucDangChon === 'cuoc-hop-duoc-giao'}
          chiXemRaSoat={mucDangChon === 'ho-so-ra-soat'}
          chiXemChuyenCap={mucDangChon === 'ho-so-cho-chuyen-cap'}
          chiXemPhanHoi={mucDangChon === 'phan-hoi-ke-hoach'}
          cuocHopLapLich={lichDangMoTuHoSo?.id}
          onQuayLaiHoSo={lichDangMoTuHoSo ? () => {
            setHoSoDangXem({ id: lichDangMoTuHoSo.id, tab: 'ke-hoach', chiXem: false })
            setMucDangChon(lichDangMoTuHoSo.mucTruoc)
            setLichDangMoTuHoSo(null)
          } : undefined}
          onLapLich={(id) => {
            setLichDangMoTuHoSo({ id, mucTruoc: mucDangChon })
            setMucDangChon('lich-hop')
          }}
        />
      ) : (
        <TongQuanCuocHop
          token={token}
          nguoiDung={nguoiDung}
          coTheMoDanhSach={cacMuc.some((muc) => muc.id === 'de-xuat')}
          onMoDanhSach={() => chonMuc('de-xuat')}
          cacMucXuLy={cacMuc.map((muc) => muc.id)}
          onMoMuc={chonMuc}
          onXemCuocHop={laQuanLy ? xemHoSo : undefined}
        />
      )}
    </BoCucChung>
  )
}
