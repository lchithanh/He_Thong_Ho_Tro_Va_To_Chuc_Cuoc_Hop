import NutThaoTac from './NutThaoTac.jsx'

export default function PheDuyetAction({ onClick, disabled, choPhepTrinhCapTren }) {
  return (
    <NutThaoTac loai="chinh" onClick={onClick} disabled={disabled}>
{choPhepTrinhCapTren ? 'Đồng ý trình cấp trên' : 'Duyệt kế hoạch và lịch'}    </NutThaoTac>
  )
}
