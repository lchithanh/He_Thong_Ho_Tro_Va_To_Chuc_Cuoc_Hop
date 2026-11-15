import NutThaoTac from './NutThaoTac.jsx'

export default function TuChoiAction({ onClick, disabled, tuChoiTiepNhan = false }) {
  return (
    <NutThaoTac loai="nguyHiem" onClick={onClick} disabled={disabled}>
      {tuChoiTiepNhan ? 'Từ chối tiếp nhận' : 'Từ chối kế hoạch'}
    </NutThaoTac>
  )
}
