import { dinhDangNgayGio } from '../../utils/thoiGianCuocHop.js'
import { useEffect, useState } from 'react'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import { layBienBanDaPhatHanhCuaToi } from '../../api/bienBanApi.js'
import { layKetLuanDaPhatHanhCuaToi } from '../../api/ketLuanCuocHopApi.js'
import TaiLieuKetLuan from '../thuky/TaiLieuKetLuan.jsx'
import BanXemTruocVanBan from '../thuky/BanXemTruocVanBan.jsx'
import LichCuaHoSo from '../thuky/LichCuaHoSo.jsx'
import { TEN_PHAM_VI } from '../thuky/hienThiDeXuat.js'

const LOAI_VAN_BAN = {
  'bien-ban': {
    ten: 'Biên bản',
    layDanhSach: layBienBanDaPhatHanhCuaToi,
    truongId: 'bienBanId',
  },
  'ket-luan': {
    ten: 'Kết luận',
    layDanhSach: layKetLuanDaPhatHanhCuaToi,
    truongId: 'ketLuanId',
  },
}

const TEN_HINH_THUC = { TRUC_TIEP: 'Trực tiếp', TRUC_TUYEN: 'Trực tuyến' }

function dinhDangThoiGian(giaTri) {
  return giaTri ? new Date(giaTri).toLocaleString('vi-VN') : null
}

function ChiTietBienBanDaPhatHanh({ bienBan, token }) {
  const { cuocHopId, cuocHop, taiLieu } = bienBan
  const thongTin = [
    ['Cuộc họp', bienBan.tieuDeCuocHop],
    ['Mã cuộc họp', cuocHop?.maCuocHop],
    ['Chủ trì', cuocHop?.tenNguoiChuTri],
    ['Hình thức', TEN_HINH_THUC[cuocHop?.loaiHinh] || cuocHop?.loaiHinh],
    ['Phạm vi', TEN_PHAM_VI[cuocHop?.phamViCuocHop] || cuocHop?.phamViCuocHop],
    ['Bắt đầu thực tế', dinhDangThoiGian(cuocHop?.thoiGianBatDauThucTe)],
    ['Kết thúc thực tế', dinhDangThoiGian(cuocHop?.thoiGianKetThucThucTe)],
    ['Phiên bản', bienBan.soPhienBan],
    ['Thời điểm duyệt', dinhDangThoiGian(bienBan.ngayDuyet)],
  ].filter(([, giaTri]) => giaTri != null && giaTri !== '')

  return (
    <div className="space-y-4">
      <BanXemTruocVanBan
        tenLoai="Biên bản cuộc họp"
        tieuDe={bienBan.tieuDe}
        noiDung={bienBan.noiDungChinhThuc}
        trangThai="Đã phát hành"
        tenNguoiKy={cuocHop?.tenNguoiChuTri}
        thongTin={thongTin}
      />
      <LichCuaHoSo cuocHopId={cuocHopId} loaiHinh={cuocHop?.loaiHinh} token={token} />
      <TaiLieuKetLuan loai="bien-ban" cuocHopId={cuocHopId} token={token} danhSachCoSan={taiLieu} />
    </div>
  )
}

export function DanhSachVanBanDaPhatHanh({ token, loai }) {
  const phienDongBo = useDongBoDuLieu()
  const { ten, layDanhSach, truongId } = LOAI_VAN_BAN[loai]
  const tenVietThuong = ten.toLocaleLowerCase('vi')
  const [danhSach, setDanhSach] = useState(null)
  const [loi, setLoi] = useState('')
  const [idDangXem, setIdDangXem] = useState(null)

  useEffect(() => {
    const controller = new AbortController()

    layDanhSach(token, controller.signal)
      .then((duLieu) => {
        if (!controller.signal.aborted) {
          setDanhSach(duLieu)
          setLoi('')
        }
      })
      .catch((loiTai) => {
        if (!controller.signal.aborted) setLoi(loiTai.message)
      })

    return () => controller.abort()
  }, [token, layDanhSach, phienDongBo])

  return (
    <section className="space-y-4 rounded-xl border border-slate-200 bg-white p-5">
      <h2 className="text-lg font-bold">{ten} đã phát hành</h2>

      {loi && <p role="alert" className="text-sm text-red-800">{loi}</p>}
      {!loi && danhSach === null && (
        <p role="status" className="text-sm text-slate-600">Đang tải {tenVietThuong}…</p>
      )}
      {danhSach?.length === 0 && (
        <p className="text-sm text-slate-600">Bạn chưa có {tenVietThuong} đã phát hành.</p>
      )}

      {danhSach?.map((vanBan) => {
        const id = vanBan[truongId]
        const dangXem = idDangXem === id

        return (
          <article key={id} className="rounded-lg border border-slate-200 p-4">
            <h3 className="font-semibold">{vanBan.tieuDeCuocHop || `Cuộc họp #${vanBan.cuocHopId}`}</h3>
            <p className="text-sm text-slate-600">Phát hành · {dinhDangNgayGio(vanBan.ngayPhatHanh)}</p>
            <button
              type="button"
              aria-expanded={dangXem}
              onClick={() => setIdDangXem(dangXem ? null : id)}
              className="mt-2 text-sm font-semibold text-emerald-800"
            >
              {dangXem ? 'Đóng chi tiết' : `Xem ${tenVietThuong}`}
            </button>

            {dangXem && (
              <div className="mt-3 space-y-4 rounded-xl border border-slate-100 bg-slate-50 p-4 sm:p-6">
                {loai === 'bien-ban' ? (
                  <ChiTietBienBanDaPhatHanh bienBan={vanBan} token={token} />
                ) : (
                  <>
                    <h4 className="text-lg font-bold">{vanBan.tieuDe}</h4>
                    <p className="whitespace-pre-wrap break-words text-sm leading-7">
                      {vanBan.noiDung || 'Xem nội dung trong tệp đính kèm.'}
                    </p>
                    <TaiLieuKetLuan cuocHopId={vanBan.cuocHopId} token={token} />
                  </>
                )}
              </div>
            )}
          </article>
        )
      })}
    </section>
  )
}

export default function VanBanDaPhatHanh({ token }) {
  const [loai, setLoai] = useState('ket-luan')

  return (
    <div className="space-y-4">
      <header className="rounded-xl border border-slate-200 bg-white px-5 pt-4">
        <h1 className="text-xl font-bold text-slate-900">Văn bản đã phát hành</h1>
        <nav aria-label="Loại văn bản đã phát hành" className="mt-3 flex flex-wrap gap-2">
          {Object.entries(LOAI_VAN_BAN).map(([id, { ten }]) => (
            <button
              type="button"
              key={id}
              aria-pressed={id === loai}
              onClick={() => setLoai(id)}
              className={`border-b-2 px-3 py-2.5 text-sm font-semibold ${id === loai
                ? 'border-emerald-700 text-emerald-800'
                : 'border-transparent text-slate-600 hover:text-slate-900'}`}
            >
              {ten}
            </button>
          ))}
        </nav>
      </header>
      <DanhSachVanBanDaPhatHanh key={`${loai}:${token}`} token={token} loai={loai} />
    </div>
  )
}
