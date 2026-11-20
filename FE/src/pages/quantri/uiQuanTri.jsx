import { useEffect, useRef } from 'react'
import Search from 'lucide-react/dist/esm/icons/search.mjs'
import X from 'lucide-react/dist/esm/icons/x.mjs'

export function TieuDeTrang({ ten, moTa, children }) {
  return (
    <header className="adm-page-title">
      <div><h1>{ten}</h1>{moTa && <p>{moTa}</p>}</div>
      {children}
    </header>
  )
}

export function TimKiem({ tuKhoa, onDoi, nhan = 'Tìm kiếm' }) {
  return (
    <label className="adm-search">
      <Search size={17} aria-hidden="true" />
      <input type="search" aria-label={nhan} placeholder={nhan} value={tuKhoa}
        onChange={(event) => onDoi(event.target.value)} />
    </label>
  )
}

export function TrangThai({ ten, mau = 'xam' }) {
  return <span className="adm-status" data-mau={mau}>{ten}</span>
}

export function PhanHoi({ loi, thongBao, onTaiLai }) {
  return (
    <>
      {loi && <div className="adm-feedback adm-error">
        <span role="alert">{loi}</span>
        {onTaiLai && <button type="button" onClick={onTaiLai}>Thử lại</button>}
      </div>}
      {thongBao && <p role="status" className="adm-feedback adm-success">{thongBao}</p>}
    </>
  )
}

export function NganChinhSua({ tieuDe, moTa, dangLuu, onDong, children }) {
  const cuaSo = useRef(null)

  useEffect(() => {
    const dialog = cuaSo.current
    dialog.showModal()
    return () => dialog.close()
  }, [])

  return (
    <dialog ref={cuaSo} className="adm-drawer" aria-labelledby="adm-drawer-title"
      onCancel={(event) => { event.preventDefault(); if (!dangLuu) onDong() }}>
      <header>
        <div>
          <h2 id="adm-drawer-title">{tieuDe}</h2>
          {moTa && <p>{moTa}</p>}
        </div>
        <button type="button" className="adm-icon-btn" disabled={dangLuu} onClick={onDong} aria-label="Đóng">
          <X size={20} />
        </button>
      </header>
      <div className="adm-drawer-body">{children}</div>
    </dialog>
  )
}
