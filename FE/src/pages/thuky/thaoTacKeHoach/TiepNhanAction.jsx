import NutThaoTac from './NutThaoTac.jsx'

export default function TiepNhanAction({ onClick, disabled }) {
  return <NutThaoTac loai="chinh" onClick={onClick} disabled={disabled}>Tiếp nhận rà soát</NutThaoTac>
}
