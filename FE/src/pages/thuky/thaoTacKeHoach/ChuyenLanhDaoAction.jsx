import NutThaoTac from './NutThaoTac.jsx'

export default function ChuyenLanhDaoAction({ onClick, disabled }) {
  return <NutThaoTac loai="chinh" onClick={onClick} disabled={disabled}>Trình người duyệt</NutThaoTac>
}
