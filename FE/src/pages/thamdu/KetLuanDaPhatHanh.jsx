import { DanhSachVanBanDaPhatHanh } from './VanBanDaPhatHanh.jsx'

export default function KetLuanDaPhatHanh({ token }) {
  return <DanhSachVanBanDaPhatHanh key={token} token={token} loai="ket-luan" />
}
