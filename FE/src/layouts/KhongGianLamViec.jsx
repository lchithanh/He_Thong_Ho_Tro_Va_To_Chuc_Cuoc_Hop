import { useCallback, useEffect, useState } from 'react'
import CuocHopDangDienRa from '../pages/thamdu/CuocHopDangDienRa.jsx'
import { dinhDangNgayGio } from '../utils/thoiGianCuocHop.js'
import PhongCuocHop from '../pages/thamdu/PhongCuocHop.jsx'
import BoCucChung from './BoCucChung.jsx'
import TongQuanCuocHop from '../pages/TongQuanCuocHop.jsx'
import { LOP_NUT_CHINH } from '../components/lopBieuMau.js'
import ChiTietDeXuat from '../pages/thuky/ChiTietDeXuat.jsx'
import { useKetNoiHoSo } from '../services/useKetNoiHoSo.js'
import { layMenuKhongGianLamViec } from './menuKhongGianLamViec.js'

function docPhongHopTuHash() {
  return /^#cuoc-hop\/([1-9]\d{0,9}(?:\/tai-lieu)?)$/.exec(window.location.hash)?.[1] ?? null
}

export default function KhongGianLamViec({ nguoiDung, loi, onDangXuat }) {
  const { maVaiTro, token } = nguoiDung
  useKetNoiHoSo(token)
  const laQuanLy = maVaiTro === 'LANH_DAO' || (maVaiTro === 'CAN_BO' && nguoiDung.truongPhong)
  const [viecDaChon, setViecDaChon] = useState(null)
  const [mucDangChon, setMucDangChon] = useState('tong-quan')
  const [lichDangMoTuHoSo, setLichDangMoTuHoSo] = useState(null)
  const [hoSoDangXem, setHoSoDangXem] = useState(null)
  const [hopVuaKetThuc, setHopVuaKetThuc] = useState(null)
  const [phongHopId, setPhongHopId] = useState(docPhongHopTuHash)
  useEffect(() => {
    const capNhat = () => setPhongHopId(docPhongHopTuHash())
    window.addEventListener('hashchange', capNhat)
    return () => window.removeEventListener('hashchange', capNhat)
  }, [])

  const cacMuc = layMenuKhongGianLamViec(maVaiTro, laQuanLy)

  const mucHienTai = cacMuc.find((muc) => muc.id === mucDangChon)
  const TrangHienTai = mucHienTai?.Trang

  function chonMuc(id, hoSo) {
    setViecDaChon(Array.isArray(hoSo) ? hoSo : null)
    setHoSoDangXem(null)
    setLichDangMoTuHoSo(null)
    setMucDangChon(id)
  }

  function xemHoSo(id) {
    setHoSoDangXem({ id, tab: 'thong-tin', chiXem: true })
  }


  function moPhongHop(id) {
    window.location.hash = `cuoc-hop/${id}`
    setPhongHopId(String(id))
    window.scrollTo(0, 0)
  }

  const veTongQuan = useCallback(() => {
    window.history.replaceState(null, '', window.location.pathname + window.location.search)
    setPhongHopId(null)
    setHoSoDangXem(null)
    setLichDangMoTuHoSo(null)
    setMucDangChon('tong-quan')
    setViecDaChon(null)
    window.scrollTo(0, 0)
  }, [])

  const sauKhiKetThuc = useCallback((hop) => {
    setHopVuaKetThuc(hop)
    veTongQuan()
  }, [veTongQuan])

  function moHoSoTuPhongHop(id, laThuKyToChuc) {
    veTongQuan()
    setHoSoDangXem({ id, tab: 'ke-hoach', chiXem: !laThuKyToChuc })
  }

  if (phongHopId) {
    const [id, mucHoSo] = phongHopId.split('/')
    return <PhongCuocHop key={phongHopId} cuocHopId={Number(id)} nguoiDung={nguoiDung} chiXemHoSo={mucHoSo === 'tai-lieu'}
      onQuayLai={veTongQuan} onDaKetThuc={sauKhiKetThuc} onMoHoSo={moHoSoTuPhongHop} />
  }

  const oTongQuan = !hoSoDangXem && !TrangHienTai

  return (
    <BoCucChung
      nguoiDung={nguoiDung}
      onDangXuat={onDangXuat}
      giaoDienToi={oTongQuan}
      dieuHuong={cacMuc}
      mucDangChon={mucDangChon}
      onChonMuc={chonMuc}
    >
      <CuocHopDangDienRa token={token} onMoPhongHop={moPhongHop} oTongQuan={oTongQuan} />
      {oTongQuan && hopVuaKetThuc && (
        <section aria-label="Cuộc họp vừa kết thúc" className="rounded-xl border border-blue-200 phong-toi:border-phong-vien-xanh bg-blue-50 phong-toi:bg-phong-nhan-xanh p-4">
          <p role="status" className="font-semibold">Cuộc họp «{hopVuaKetThuc.tieuDe}» đã kết thúc.</p>
          <p className="text-sm text-slate-600 phong-toi:text-phong-chu-phu">{dinhDangNgayGio(hopVuaKetThuc.thoiGianKetThucThucTe)}</p>
          <a href={`#cuoc-hop/${hopVuaKetThuc.cuocHopId}/tai-lieu`}
            className="mt-2 inline-block text-sm font-semibold text-blue-900 underline phong-toi:text-phong-xanh">
            Hồ sơ và tài liệu cuộc họp
          </a>
          {hopVuaKetThuc.laThuKyToChuc && <p className="mt-1 text-sm">Soạn biên bản và kết luận, trình duyệt rồi theo dõi phát hành.</p>}
          {(hopVuaKetThuc.laChuTri || hopVuaKetThuc.laThuKyToChuc) && (
            <div className="mt-3 flex flex-wrap gap-2">
              {[['bien-ban', 'Mở biên bản'], ['ket-luan', 'Mở kết luận']].map(([tab, ten]) => (
                <button type="button" key={tab} className="rounded-lg border border-blue-200 px-3 py-2 text-sm font-semibold text-blue-900 hover:bg-blue-100 phong-toi:border-phong-vien-xanh phong-toi:text-phong-xanh phong-toi:hover:bg-phong-mat-noi"
                  onClick={() => setHoSoDangXem({ id: hopVuaKetThuc.cuocHopId, tab, chiXem: !hopVuaKetThuc.laThuKyToChuc })}>
                  {ten}
                </button>
              ))}
            </div>
          )}

        </section>
      )}
      {loi && (
        <p role="alert" className="rounded-xl border border-red-200 phong-toi:border-phong-vien-do bg-red-50 phong-toi:bg-phong-nhan-do p-4 text-sm text-red-800 phong-toi:text-phong-do">
          {loi}
        </p>
      )}

      {!hoSoDangXem && !TrangHienTai && laQuanLy && (
        <section className="flex flex-wrap items-center justify-between gap-3 rounded-xl border border-slate-200 phong-toi:border-phong-vien bg-white phong-toi:bg-phong-mat p-4">
          <div>
            <h2 className="font-semibold">{maVaiTro === 'LANH_DAO' ? 'Không gian lãnh đạo' : 'Không gian trưởng phòng'}</h2>
            <p className="mt-1 text-sm text-slate-600 phong-toi:text-phong-chu-phu">
              Thư ký mặc định · {nguoiDung.tenThuKyMacDinh || 'Chưa được phân công'}
            </p>
          </div>
          <button type="button" className={LOP_NUT_CHINH} onClick={() => setMucDangChon('de-xuat-cua-toi')}>Đề xuất cuộc họp</button>
        </section>
      )}

      {hoSoDangXem ? (
        <ChiTietDeXuat key={hoSoDangXem.id} cuocHopId={hoSoDangXem.id} token={token} nguoiDung={nguoiDung}
          onMoPhongHop={moPhongHop} chiXem={hoSoDangXem.chiXem} tabBanDau={hoSoDangXem.tab} onQuayLai={() => setHoSoDangXem(null)}
          onLapLich={(id) => {
            setLichDangMoTuHoSo({ id, mucTruoc: mucDangChon })
            setHoSoDangXem(null)
            setMucDangChon('lich-hop')
          }} />
      ) : TrangHienTai ? (
        <TrangHienTai
          key={mucDangChon}
          viecDaChon={viecDaChon}
          onMoPhongHop={moPhongHop}
          onXemCuocHop={laQuanLy ? xemHoSo : undefined}
          token={token}
          nguoiDung={nguoiDung}
          chiXemBanGiao={mucDangChon === 'cuoc-hop-duoc-giao'}
          chiXemRaSoat={mucDangChon === 'ho-so-ra-soat'}
          chiXemChuyenCap={mucDangChon === 'ho-so-chuyen-cap'}
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
          coTheMoDanhSach={cacMuc.some((muc) => muc.id === 'de-xuat')}
          onMoDanhSach={() => chonMuc('de-xuat')}
          cacMucXuLy={cacMuc.map((muc) => muc.id)}
          coQuyenDuyetKeHoach={laQuanLy}
          onMoMuc={chonMuc}
          onXemCuocHop={laQuanLy ? xemHoSo : undefined}
          onSoanVanBan={(id, tab) => setHoSoDangXem({ id, tab, chiXem: false })}
        />
      )}
    </BoCucChung>
  )
}
