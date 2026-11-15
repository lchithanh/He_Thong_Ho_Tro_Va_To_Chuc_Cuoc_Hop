export default function NutThaoTac({ children, onClick, disabled, loai = 'phu' }) {
  const mauSac = {
    chinh: 'border-emerald-800 bg-emerald-800 text-white hover:bg-emerald-900',
    phu: 'border-slate-300 bg-white text-slate-800 hover:bg-slate-50',
    nguyHiem: 'border-rose-300 bg-white text-rose-800 hover:bg-rose-50',
  }

  return (
    <button
      type="button"
      disabled={disabled}
      onClick={onClick}
      className={`rounded-lg border px-3 py-2 text-sm font-semibold transition disabled:cursor-not-allowed disabled:opacity-60 ${mauSac[loai]}`}
    >
      {children}
    </button>
  )
}
